package com.axiom.app.presentation.activation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.domain.repository.ActivationRepository
import com.axiom.app.domain.repository.ActivationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.axiom.app.data.local.AxiomPreferences

data class ActivationUiState(
    val email: String = "",
    val password: String = "",
    val isSignUpMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isActivated: Boolean = false,
    val logs: List<String> = emptyList()
)

@HiltViewModel
class ActivationViewModel @Inject constructor(
    private val activationRepository: ActivationRepository,
    private val preferences: AxiomPreferences,
    private val cloudSyncRepository: com.axiom.app.domain.repository.CloudSyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivationUiState())
    val uiState: StateFlow<ActivationUiState> = _uiState

    init {
        viewModelScope.launch {
            activationRepository.isActivated().collect { activated ->
                _uiState.update { it.copy(isActivated = activated) }
            }
        }
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, error = null) }
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, error = null) }
    }

    fun setSignUpMode(isSignUp: Boolean) {
        _uiState.update { 
            it.copy(
                isSignUpMode = isSignUp,
                error = null
            )
        }
    }

    fun handleGoogleSignInSuccess(idToken: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    error = null,
                    logs = listOf("[GOOGLE] Authenticating via Google Secure Link...")
                )
            }
            kotlinx.coroutines.delay(1000)
            val result = activationRepository.loginWithGoogle(idToken)
            when (result) {
                is ActivationResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isActivated = true
                        )
                    }
                }
                is ActivationResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun triggerActivationWithCode(code: String, email: String) {
        if (code.isBlank()) {
            _uiState.update { it.copy(error = "[DENIED] Activation code cannot be empty.") }
            return
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    logs = listOf(
                        "[SYSTEM] Verifying activation code...",
                        "[CONNECT] Upgrading anonymous session — preserving hunter data..."
                    )
                )
            }
            kotlinx.coroutines.delay(1500)
            // upgradeAnonymousToEmail حفظ missions/streak/league داده‌های anonymous
            // loginWithEmailPassword همه چیز رو پاک می‌کنه — نباید اینجا استفاده بشه
            val result = activationRepository.upgradeAnonymousToEmail(
                email = email,
                password = code
            )
            when (result) {
                is ActivationResult.Success -> {
                    cloudSyncRepository.backupProgress()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isActivated = true,
                            logs = it.logs + "[SUCCESS] Code verified. Hunter profile awakened. Cloud sync complete."
                        )
                    }
                }
                is ActivationResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Invalid activation code.",
                            logs = it.logs + "[DENIED] Code rejected. Verify your activation code."
                        )
                    }
                }
            }
        }
    }

    fun triggerActivation() {
        val currentState = _uiState.value
        val hasSupabase = activationRepository.hasSupabaseCredentials()
        
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    error = null,
                    logs = listOf(
                        "[SYSTEM] Initiating cognitive awakening protocol...",
                        if (hasSupabase) {
                            if (currentState.isSignUpMode) "[CONNECT] Registering hunter soul in Supabase database..."
                            else "[CONNECT] Restoring active soul link on Supabase auth channels..." 
                        } else "[OFFLINE] Supabase link missing. Awakening simulated profile locally..."
                    )
                )
            }

            kotlinx.coroutines.delay(1500)

            val result = if (currentState.isSignUpMode) {
                // Upgrades the silent anonymous session in place when one
                // exists (preserving league history); falls back to a
                // normal sign-up otherwise.
                activationRepository.upgradeAnonymousToEmail(
                    email = currentState.email,
                    password = currentState.password
                )
            } else {
                activationRepository.loginWithEmailPassword(
                    email = currentState.email,
                    password = currentState.password
                )
            }

            when (result) {
                is ActivationResult.Success -> {
                    if (currentState.isSignUpMode) {
                        cloudSyncRepository.backupProgress()
                    } else {
                        cloudSyncRepository.restoreProgress()
                    }
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isActivated = true
                        )
                    }
                }
                is ActivationResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}
