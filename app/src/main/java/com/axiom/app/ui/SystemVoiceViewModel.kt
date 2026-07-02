package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.core.ai.SystemVoiceEngine
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.usecase.GetHunterProfileUseCase
import com.axiom.app.domain.usecase.GetMissionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Local-only chat entry — not persisted to DB
data class ChatEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false
)

@HiltViewModel
class SystemVoiceViewModel @Inject constructor(
    private val engine: SystemVoiceEngine,
    val preferences: AxiomPreferences,
    private val getHunterProfile: GetHunterProfileUseCase,
    private val getMissions: GetMissionsUseCase
) : ViewModel() {

    private val _isLoading   = MutableStateFlow(false)
    private val _chat        = MutableStateFlow<List<ChatEntry>>(emptyList())
    private val _apiKeySaved = MutableStateFlow(false)

    val isLoading: StateFlow<Boolean>     = _isLoading.asStateFlow()
    val chat: StateFlow<List<ChatEntry>>  = _chat.asStateFlow()
    val apiKeySaved: StateFlow<Boolean>   = _apiKeySaved.asStateFlow()

    val hunterLevel: StateFlow<Int> = getHunterProfile()
        .map { it?.level ?: 1 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val isDevBypass: StateFlow<Boolean> = preferences.devBypassFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val hasApiKey: StateFlow<Boolean> = preferences.geminiApiKeyFlow
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val systemVoiceMode: StateFlow<String> = preferences.systemVoiceModeFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, "COLD")

    val dailyBriefing: StateFlow<String?> = preferences.dailyBriefingFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        loadBriefingIfNeeded()
    }

    fun loadBriefingIfNeeded() {
        viewModelScope.launch {
            if (!engine.hasApiKey()) return@launch
            if (preferences.isBriefingFreshToday()) return@launch
            val hunter = getHunterProfile().first() ?: return@launch
            val streak = preferences.streakFlow.first()
            val count  = getMissions(activeOnly = true).first().size
            val text   = engine.generateDailyBriefing(hunter, streak, count)
            preferences.saveDailyBriefing(text)
        }
    }

    fun setSystemVoiceMode(mode: String) {
        viewModelScope.launch {
            preferences.setSystemVoiceMode(mode)
            if (!engine.hasApiKey()) return@launch
            val hunter = getHunterProfile().first() ?: return@launch
            val streak = preferences.streakFlow.first()
            val count  = getMissions(activeOnly = true).first().size
            val text   = engine.generateDailyBriefing(hunter, streak, count)
            preferences.saveDailyBriefing(text)
        }
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            preferences.setGeminiApiKey(key)
            _apiKeySaved.value = true
            loadBriefingIfNeeded()
        }
    }

    fun clearApiKey() {
        viewModelScope.launch { preferences.clearGeminiApiKey() }
    }

    fun askSystem(question: String) {
        viewModelScope.launch {
            val hunter = getHunterProfile().first() ?: return@launch
            val streak = preferences.streakFlow.first()
            _chat.value = _chat.value + ChatEntry(text = question, isUser = true)
            _isLoading.value = true
            
            val initialResponseEntry = ChatEntry(text = "", isUser = false, isStreaming = true)
            _chat.value = _chat.value + initialResponseEntry
            _isLoading.value = false
            
            var accumulatedText = ""
            engine.askSystemStream(hunter, streak, question).collect { chunk ->
                accumulatedText += chunk
                _chat.value = _chat.value.mapIndexed { index, entry ->
                    if (index == _chat.value.lastIndex) {
                        entry.copy(text = accumulatedText)
                    } else {
                        entry
                    }
                }
            }
            
            _chat.value = _chat.value.mapIndexed { index, entry ->
                if (index == _chat.value.lastIndex) {
                    entry.copy(isStreaming = false)
                } else {
                    entry
                }
            }
        }
    }
}
