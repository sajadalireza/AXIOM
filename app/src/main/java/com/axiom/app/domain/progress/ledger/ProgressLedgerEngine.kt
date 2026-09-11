package com.axiom.app.domain.progress.ledger

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Result of attempting to append a [LedgerEntry] to the Progress Ledger.
 */
sealed interface ProgressAppendResult {
    data class Success(val snapshot: ProgressSnapshot, val entry: LedgerEntry) : ProgressAppendResult
    data class Rejected(
        val reason: String,
        val violation: ProgressAntiFarmingPolicy.AntiFarmingViolation
    ) : ProgressAppendResult
}

/**
 * Interface representing the authoritative Progress Ledger engine.
 *
 * Maintains append-only progress events, applies anti-farming filters, and serves
 * real-time deterministic [ProgressSnapshot] projections.
 */
interface ProgressLedgerEngine {
    val snapshot: StateFlow<ProgressSnapshot>
    val history: StateFlow<List<LedgerEntry>>

    suspend fun append(entry: LedgerEntry): ProgressAppendResult
    suspend fun replay(entries: List<LedgerEntry>): ProgressSnapshot
    fun getSnapshot(): ProgressSnapshot
    fun getHistory(): List<LedgerEntry>
}

/**
 * Thread-safe in-memory implementation of [ProgressLedgerEngine].
 */
class InMemoryProgressLedgerEngine(
    private val antiFarmingPolicy: ProgressAntiFarmingPolicy = ProgressAntiFarmingPolicy(),
    initialEntries: List<LedgerEntry> = emptyList()
) : ProgressLedgerEngine {

    private val mutex = Mutex()
    private val _history = MutableStateFlow<List<LedgerEntry>>(emptyList())
    override val history: StateFlow<List<LedgerEntry>> = _history.asStateFlow()

    private val _snapshot = MutableStateFlow(ProgressSnapshot.EMPTY)
    override val snapshot: StateFlow<ProgressSnapshot> = _snapshot.asStateFlow()

    init {
        if (initialEntries.isNotEmpty()) {
            _history.value = initialEntries
            _snapshot.value = ProgressProjectionReducer.reduce(initialEntries)
        }
    }

    override suspend fun append(entry: LedgerEntry): ProgressAppendResult = mutex.withLock {
        val currentHistory = _history.value
        when (val validation = antiFarmingPolicy.validate(entry, currentHistory)) {
            is ProgressAntiFarmingPolicy.ValidationResult.Rejected -> {
                _snapshot.value = _snapshot.value.copy(
                    antiFarmingViolationsCount = _snapshot.value.antiFarmingViolationsCount + 1
                )
                ProgressAppendResult.Rejected(
                    reason = validation.reason,
                    violation = validation.violation
                )
            }
            is ProgressAntiFarmingPolicy.ValidationResult.Valid -> {
                val newHistory = currentHistory + entry
                _history.value = newHistory
                val newSnapshot = ProgressProjectionReducer.step(_snapshot.value, entry)
                _snapshot.value = newSnapshot
                ProgressAppendResult.Success(newSnapshot, entry)
            }
        }
    }

    override suspend fun replay(entries: List<LedgerEntry>): ProgressSnapshot = mutex.withLock {
        val sortedEntries = entries.sortedWith(
            compareBy<LedgerEntry> { it.timestamp }.thenBy { it.id }
        )
        val validEntries = mutableListOf<LedgerEntry>()
        var violations = 0
        for (entry in sortedEntries) {
            when (antiFarmingPolicy.validate(entry, validEntries)) {
                is ProgressAntiFarmingPolicy.ValidationResult.Valid -> validEntries.add(entry)
                is ProgressAntiFarmingPolicy.ValidationResult.Rejected -> violations++
            }
        }

        val replayedSnapshot = ProgressProjectionReducer.reduce(validEntries).copy(
            antiFarmingViolationsCount = violations
        )
        _history.value = validEntries
        _snapshot.value = replayedSnapshot
        replayedSnapshot
    }

    override fun getSnapshot(): ProgressSnapshot = _snapshot.value

    override fun getHistory(): List<LedgerEntry> = _history.value
}
