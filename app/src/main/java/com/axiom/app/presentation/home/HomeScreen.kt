package com.axiom.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.presentation.home.components.*
import com.axiom.app.ui.components.DailyLoginBonusBanner
import com.axiom.app.ui.HomeUiState
import com.axiom.app.ui.HomeViewModel
import com.axiom.app.ui.AxiomViewModel
import com.axiom.app.ui.VitalsViewModel
import com.axiom.app.ui.theme.*

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    axiomViewModel: AxiomViewModel = hiltViewModel(),
    vitalsViewModel: VitalsViewModel = hiltViewModel()
) {
    val state by viewModel.homeState.collectAsStateWithLifecycle()
    val colors = LocalAxiomColors.current
    val dailyLoginBonusGranted by axiomViewModel.dailyLoginBonusGranted.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.voidBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DailyLoginBonusBanner(visible = dailyLoginBonusGranted)
            Box(modifier = Modifier.weight(1f)) {
                when (val s = state) {
                    is HomeUiState.Loading -> LoadingShimmerScreen()
                    is HomeUiState.Success -> SuccessContent(
                        state = s,
                        onNavigate = onNavigate,
                        viewModel = viewModel,
                        axiomViewModel = axiomViewModel,
                        vitalsViewModel = vitalsViewModel,
                        missionsViewModel = hiltViewModel()
                    )
                    is HomeUiState.Error -> ErrorScreen(message = s.message)
                }
            }
        }

        com.axiom.app.ui.components.BurnoutOverlay(viewModel = vitalsViewModel)
        com.axiom.app.ui.components.EnergyPromptOverlay(viewModel = vitalsViewModel)
    }
}
