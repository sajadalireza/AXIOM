package com.axiom.app.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.R
import com.axiom.app.presentation.home.components.*
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
    val isDark = colors.voidBlack == AxiomDarkColors.voidBlack

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.voidBlack)
    ) {
        // Atmospheric artwork layer (V3-M05): Mountain Horizon & Celestial Depth in Dark Theme
        if (isDark) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(560.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_mountain_monolith),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.30f),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter
                )
                // Gradient scrim ensuring smooth fade into voidBlack background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    colors.voidBlack.copy(alpha = 0.28f),
                                    colors.voidBlack.copy(alpha = 0.76f),
                                    colors.voidBlack
                                )
                            )
                        )
                )
            }
        }

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
