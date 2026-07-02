package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.core.XionEvent
import com.axiom.app.core.XionEventBus
import com.axiom.app.core.ai.SystemVoiceEngine
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.usecase.GetHunterProfileUseCase
import com.axiom.app.domain.usecase.GetMissionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.domain.engine.XPEngine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class XionUiSnapshot(
    val hunterName: String = "HUNTER",
    val rankLabel: String = "E-RANK",
    val streakDays: Int = 0,
    val activeMissionCount: Int = 0,
    val completedTodayCount: Int = 0,
    val inactiveDays: Int = 0,
    val totalXP: Long = 0L
)

@HiltViewModel
class XionViewModel @Inject constructor(
    private val getHunterProfile: GetHunterProfileUseCase,
    private val getMissions: GetMissionsUseCase,
    private val eventBus: XionEventBus,
    private val engine: SystemVoiceEngine,   // XION-4: needed for chat
    private val hunterRepository: HunterRepository,
    val preferences: AxiomPreferences
) : ViewModel() {

    // Dev bypass exposure
    val devBypassFlow: StateFlow<Boolean> = preferences.devBypassFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    // ── Snapshot ───────────────────────────────────────────────────────────
    private val _snapshot = MutableStateFlow(XionUiSnapshot())
    val snapshot: StateFlow<XionUiSnapshot> = _snapshot.asStateFlow()

    val languageState: StateFlow<String> = preferences.languageFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "en")

    val contextualBubble: StateFlow<String> = combine(
        _snapshot,
        languageState
    ) { s, language ->
        val isFa = language == "fa"
        if (isFa) {
            when {
                s.inactiveDays >= 3 ->
                    "[ سیستم ] ${s.inactiveDays} روز غیبت شناسایی شد. پروتکل جریمه فعال شد، ${s.hunterName}."
                s.streakDays == 0 ->
                    "هیچ زنجیره فعالی ثبت نشده، ${s.hunterName}. اولین پروتکل مأموریت امروز را قبل از نیمه‌شب ثبت کنید."
                s.completedTodayCount > 0 ->
                    "${s.completedTodayCount} مأموریت امروز پاکسازی شد. ${s.hunterName}، سیستم نظارت می‌کند."
                s.activeMissionCount == 0 ->
                    "صف مأموریت‌ها خالی است، ${s.hunterName}. حداقل یک مأموریت برای تداوم رتبه اضافه کنید."
                s.streakDays >= 30 ->
                    "[ سیستم ] زنجیره شگفت‌انگیز ${s.streakDays} روزه تثبیت شد. ${s.hunterName} — این پدیده‌ای کمیاب است."
                s.streakDays in 7..29 ->
                    "زنجیره: ${s.streakDays} روز. ${s.hunterName}، رفتار انضباطی شما در حال پایدار شدن است."
                else ->
                    "${s.activeMissionCount} مأموریت فعال است. زنجیره: ${s.streakDays} روز. پیوستگی زنجیره را حفظ کن، ${s.hunterName}."
            }
        } else {
            when {
                s.inactiveDays >= 3 ->
                    "[ SYSTEM ] ${s.inactiveDays}-day absence detected. Penalty Protocol initiated, ${s.hunterName}."
                s.streakDays == 0 ->
                    "No active streak, ${s.hunterName}. Begin today's protocol before midnight."
                s.completedTodayCount > 0 ->
                    "${s.completedTodayCount} missions cleared today. ${s.hunterName}, the system is watching."
                s.activeMissionCount == 0 ->
                    "Mission queue empty, ${s.hunterName}. Add at least one protocol to maintain rank."
                s.streakDays >= 30 ->
                    "[ SYSTEM ] ${s.streakDays}-day streak archived. ${s.hunterName} — this is rare."
                s.streakDays in 7..29 ->
                    "Streak: ${s.streakDays} days. ${s.hunterName}, you are becoming consistent."
                else ->
                    "${s.activeMissionCount} missions active. Streak: ${s.streakDays} days. Maintain cadence, ${s.hunterName}."
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, "Awaiting hunter data...")

    // ── XION-3: Event bus ─────────────────────────────────────────────────
    private val _activeEvent = MutableStateFlow<XionEvent?>(null)
    val activeEvent: StateFlow<XionEvent?> = _activeEvent.asStateFlow()

    // ── XION-4: Chat history ──────────────────────────────────────────────
    // Pair<isUser, text>
    private val _chatHistory = MutableStateFlow<List<Pair<Boolean, String>>>(emptyList())
    val chatHistory: StateFlow<List<Pair<Boolean, String>>> = _chatHistory.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    init {
        refresh()
        viewModelScope.launch {
            eventBus.events.collect { event ->
                _activeEvent.value = event
                refresh()
                delay(4000)
                if (_activeEvent.value == event) _activeEvent.value = null
            }
        }
    }

    // ── XION-3 helper ─────────────────────────────────────────────────────
    fun bubbleForEvent(event: XionEvent): String {
        val isFa = languageState.value == "fa"
        return if (isFa) {
            when (event) {
                is XionEvent.MissionCompleted ->
                    "[ +${event.xpGained} امتیاز تجربه ] ${event.missionTitle.uppercase()} با موفقیت ثبت شد. تاییدیه کمیابی ${event.rarity.uppercase()}."
                is XionEvent.RankUp ->
                    "[ ارتقاء رتبه ] ${event.oldRank} → ${event.newRank}. هانتر، سیستم تغییرات فاز شناختی شما را ثبت نمود."
                is XionEvent.StreakBroken ->
                    "[ نقض پروتکل ] زنجیره پیشین متوقف شد. شمارش مجدد آغاز می‌گردد، ${_snapshot.value.hunterName}."
                is XionEvent.StreakMilestone ->
                    "[ نقطه عطف روزهای پیاپی ] زنجیره ${event.days} روزه تایید شد. ثبات رفتاری خارق‌العاده."
                is XionEvent.DailyLoginFirst ->
                    "هانتر ${_snapshot.value.hunterName} — دریچه پروتکل روزانه فعال است. آغاز برای پاکسازی."
                is XionEvent.LevelUp ->
                    "[ سطح جدید ${event.newLevel} ] افزایش ظرفیت شبکه عصبی. آستانه‌های ارتباطی بازگشایی شدند."
            }
        } else {
            when (event) {
                is XionEvent.MissionCompleted ->
                    "[ +${event.xpGained} XP ] ${event.missionTitle.uppercase()} archived. ${event.rarity.uppercase()} tier confirmed."
                is XionEvent.RankUp ->
                    "[ RANK UP ] ${event.oldRank} → ${event.newRank}. Hunter, the system acknowledges."
                is XionEvent.StreakBroken ->
                    "[ BREACH ] Previous streak terminated. Restarting count, ${_snapshot.value.hunterName}."
                is XionEvent.StreakMilestone ->
                    "[ MILESTONE ] ${event.days}-day streak confirmed. Exceptional behavioral consistency."
                is XionEvent.DailyLoginFirst ->
                    "Hunter ${_snapshot.value.hunterName} — daily protocol window is active. Execute."
                is XionEvent.LevelUp ->
                    "[ LEVEL ${event.newLevel} ] Neural capacity expanding. New thresholds unlocked."
            }
        }
    }

    // ── XION-4: Chat ──────────────────────────────────────────────────────
    fun sendChat(message: String) {
        viewModelScope.launch {
            _chatHistory.value = _chatHistory.value + Pair(true, message)
            _isChatLoading.value = true
            val s = _snapshot.value
            val hunter: Hunter = getHunterProfile().first() ?: run {
                _isChatLoading.value = false
                return@launch
            }
            val reply = engine.askSystem(hunter, s.streakDays, message)
            _chatHistory.value = _chatHistory.value + Pair(false, reply)
            _isChatLoading.value = false
        }
    }

    fun clearChat() { _chatHistory.value = emptyList() }

    // ── Refresh ───────────────────────────────────────────────────────────
    fun refresh() {
        viewModelScope.launch {
            val hunter: Hunter = getHunterProfile().first() ?: return@launch
            val allMissions: List<Mission> = getMissions(activeOnly = false).first()
            val streak = preferences.streakFlow.first()

            val todayStart = (System.currentTimeMillis() / 86_400_000L) * 86_400_000L
            val completedToday = allMissions.count {
                it.status.equals("COMPLETED", ignoreCase = true) &&
                    (it.completedAt ?: 0L) >= todayStart
            }
            val lastComplete = preferences.lastCompleteTimestampFlow.first()
            val inactiveDays = if (lastComplete == 0L) 0
                else ((System.currentTimeMillis() - lastComplete) / 86_400_000L).toInt()

            _snapshot.value = XionUiSnapshot(
                hunterName          = hunter.name,
                rankLabel           = hunter.rankLabel,
                streakDays          = streak,
                activeMissionCount  = allMissions.count { it.status.equals("ACTIVE", ignoreCase = true) },
                completedTodayCount = completedToday,
                inactiveDays        = inactiveDays,
                totalXP             = hunter.totalXP
            )
        }
    }

    fun executeDevHack(command: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val trimmed = command.trim()
            val lower = trimmed.lowercase()

            when {
                lower == "opensajadaghakishi" -> {
                    preferences.setDevBypass(true)
                    onResult("◆ CHEAT ENABLED: ALL SYSTEM GATES SHATTERED ◆\nDeveloper Hack Override: ACTIVE.\n\n[ PROTOCOL SAJAD AGHAKISHI ]:\n• 3-Day cognitive lock on Skill Tree successfully broken!\n• ظرفیت عصبی فعال شد: قفل ۳ روزه درخت مهارت با موفقیت شکسته شد!\n\nType 'help' to see updated creator shortcuts.")
                }
                lower == "lockall" || lower == "lock" -> {
                    preferences.setDevBypass(false)
                    onResult("◆ CHEAT DISABLED: ALL RESTRICTIONS INITIATED ◆\nNormal protocol resumed.")
                }
                lower.startsWith("setlevel ") -> {
                    val lvlStr = lower.removePrefix("setlevel ").trim()
                    val targetLvl = lvlStr.toIntOrNull()
                    if (targetLvl != null && targetLvl in 1..100) {
                        val current = getHunterProfile().first() ?: return@launch
                        val newRank = XPEngine.calculateHunterRank(targetLvl)
                        val rankLabelWithSuffix = if (newRank.endsWith("-Rank")) newRank else "$newRank-Rank"
                        val rankColor = XPEngine.getRankColor(newRank)
                        val rankGlyph = XPEngine.getGlyphForRank(newRank)

                        val updated = current.copy(
                            level = targetLvl,
                            rankLabel = rankLabelWithSuffix,
                            rankColor = rankColor,
                            rankGlyph = rankGlyph
                        )
                        hunterRepository.updateHunterProfile(updated)
                        refresh()
                        onResult("[ COMMAND EXECUTE ]\nNeural level directly hard-set to LEVEL $targetLvl.\nRank updated to $rankLabelWithSuffix.")
                    } else {
                        onResult("ERROR: Usage: setlevel [1..100]")
                    }
                }
                lower.startsWith("setrank ") -> {
                    val rawRank = trimmed.removePrefix("setrank ").trim()
                    if (rawRank.isNotBlank()) {
                        val current = getHunterProfile().first() ?: return@launch
                        val rankSuffix = if (rawRank.lowercase().endsWith("-rank")) rawRank else "$rawRank-Rank"
                        val updated = current.copy(rankLabel = rankSuffix)
                        hunterRepository.updateHunterProfile(updated)
                        refresh()
                        onResult("[ COMMAND EXECUTE ]\nRank label directly hard-set to ${rankSuffix.uppercase()}.")
                    } else {
                        onResult("ERROR: Usage: setrank [Rank name]")
                    }
                }
                lower.startsWith("setstreak ") -> {
                    val streakStr = lower.removePrefix("setstreak ").trim()
                    val streakDays = streakStr.toIntOrNull()
                    if (streakDays != null && streakDays >= 0) {
                        preferences.setStreak(streakDays)
                        refresh()
                        onResult("[ COMMAND EXECUTE ]\nDaily loop streak direct override: $streakDays DAYS.")
                    } else {
                        onResult("ERROR: Usage: setstreak [days]")
                    }
                }
                lower.startsWith("addxp ") -> {
                    val xpStr = lower.removePrefix("addxp ").trim()
                    val amount = xpStr.toLongOrNull()
                    if (amount != null && amount > 0) {
                        val current = getHunterProfile().first() ?: return@launch
                        val totalXP = current.totalXP + amount
                        var newXP = current.currentXP + amount.toInt()
                        var newLevel = current.level
                        var nextLevelXP = XPEngine.xpNeededForLevel(newLevel).toInt()

                        while (newXP >= nextLevelXP && newLevel < 100) {
                            newXP -= nextLevelXP
                            newLevel++
                            nextLevelXP = XPEngine.xpNeededForLevel(newLevel).toInt()
                        }
                        if (newLevel >= 100) {
                            newLevel = 100
                            newXP = 0
                        }

                        val rank = XPEngine.calculateHunterRank(newLevel)
                        val rankLabelWithSuffix = if (rank.endsWith("-Rank")) rank else "$rank-Rank"
                        val rankColor = XPEngine.getRankColor(rank)
                        val rankGlyph = XPEngine.getGlyphForRank(rank)

                        val updated = current.copy(
                            level = newLevel,
                            rankLabel = rankLabelWithSuffix,
                            totalXP = totalXP,
                            currentXP = newXP,
                            xpToNextLevel = nextLevelXP,
                            progressPercent = if (newLevel >= 100) 1.0f else newXP.toFloat() / nextLevelXP.toFloat(),
                            rankColor = rankColor,
                            rankGlyph = rankGlyph
                        )
                        hunterRepository.updateHunterProfile(updated)
                        refresh()
                        onResult("[ COMMAND EXECUTE ]\nAllocated +$amount XP. Level: $newLevel, Total XP: $totalXP.")
                    } else {
                        onResult("ERROR: Usage: addxp [amount]")
                    }
                }
                lower == "godmode" -> {
                    val current = getHunterProfile().first() ?: return@launch
                    preferences.setDevBypass(true)
                    preferences.setStreak(365)
                    val updated = current.copy(
                        level = 99,
                        rankLabel = "Monarch-Rank",
                        rankColor = XPEngine.getRankColor("Monarch"),
                        rankGlyph = XPEngine.getGlyphForRank("Monarch"),
                        totalXP = 999999L
                    )
                    hunterRepository.updateHunterProfile(updated)
                    refresh()
                    onResult("[ ADMIN MONARCH SHIELD ACTIVATED ]\nAll gates open. Streak: 365 Days.\nLevel: 99. Rank: Sovereign Monarch.")
                }
                else -> {
                    onResult("UNKNOWN CONSOLE COMMAND.\nType 'help' for available commands.")
                }
            }
        }
    }
}
