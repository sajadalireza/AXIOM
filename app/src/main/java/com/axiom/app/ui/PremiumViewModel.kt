package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.data.local.AxiomPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val preferences: AxiomPreferences
) : ViewModel() {

    val isPremium: StateFlow<Boolean> = preferences.isPremiumFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val activePlan: StateFlow<String?> = preferences.premiumPlanFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun purchasePremium(plan: String) {
        viewModelScope.launch {
            preferences.setPremium(true, plan)
        }
    }

    fun downgradePremium() {
        viewModelScope.launch {
            preferences.setPremium(false)
        }
    }
}
