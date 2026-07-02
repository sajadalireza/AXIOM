package com.axiom.app.presentation.bodymap

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.ui.theme.*
import com.axiom.app.presentation.habits.DailyCheckinScreen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyMapScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BodyMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedMuscleId by viewModel.selectedMuscleId.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = if (Locale.getDefault().language == "fa") {
        listOf("اسکنر عضلات ۳بعدی", "آنالیز کالیبر", "تخمین رکورد (1RM)", "عادت روزانه")
    } else listOf("3D MUSCLE SCANNER", "CALIBER INSIGHTS", "1RM STRENGTH", "DAILY HABITS")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CALIBER HIGH-FI CORE", fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 1.sp) },
                navigationIcon = { IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = LegendaryGold) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VoidBlack, titleContentColor = LegendaryGold)
            )
        }, containerColor = VoidBlack, modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        when (val state = uiState) {
            is BodyMapUiState.Loading -> Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) { CircularProgressIndicator(color = LegendaryGold) }
            is BodyMapUiState.Empty -> Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) { Text("اطلاعاتی یافت نشد.", color = TextDim, fontFamily = JetBrainsMono) }
            is BodyMapUiState.Success -> {
                Column(Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 14.dp)) {
                    TabRow(selectedTabIndex = selectedTab, containerColor = VoidBlack, contentColor = LegendaryGold, indicator = { tabPositions -> TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab]), color = LegendaryGold) }, modifier = Modifier.padding(vertical = 8.dp)) {
                        tabTitles.forEachIndexed { index, title -> Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMono) }) }
                    }
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        when (selectedTab) {
                            0 -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Box(Modifier.weight(1f).fillMaxWidth()) { BodySilhouetteCanvas(state.muscles, selectedMuscleId, { viewModel.selectMuscle(it) }) }
                                RecoveryTimelineBar(state.muscles, Modifier.padding(bottom = 16.dp))
                            }
                            1 -> CaliberInsightsView(state.muscles)
                            2 -> StrengthOneRMView()
                            3 -> DailyCheckinScreen(onBack = { selectedTab = 0 }, showTopBar = false)
                        }
                    }
                }
                selectedMuscleId?.let { id ->
                    state.muscles.find { it.id == id }?.let { selectedMuscle ->
                        MuscleStatusPanel(selectedMuscle, { viewModel.selectMuscle(null) }, { mId, hrs, goal, feed, push -> viewModel.logTrainingSession(mId, hrs, goal, feed, push) })
                    }
                }
            }
        }
    }
}
