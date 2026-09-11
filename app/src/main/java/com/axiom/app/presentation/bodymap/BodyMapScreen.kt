package com.axiom.app.presentation.bodymap

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.R
import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.presentation.habits.DailyCheckinScreen
import com.axiom.app.ui.theme.AxiomBorder
import com.axiom.app.ui.theme.AxiomRadius
import com.axiom.app.ui.theme.AxiomSpacing
import com.axiom.app.ui.theme.FiraCode
import com.axiom.app.ui.theme.Inter
import com.axiom.app.ui.theme.LocalAxiomColors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyMapScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BodyMapViewModel = hiltViewModel()
) {
    val colors = LocalAxiomColors.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedMuscleId by viewModel.selectedMuscleId.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val isFa = Locale.getDefault().language == "fa"

    val tabTitles = if (isFa) {
        listOf("اسکنر عضلات ۳بعدی", "آنالیز کالیبر", "تخمین رکورد (1RM)", "عادت روزانه")
    } else {
        listOf("3D MUSCLE SCANNER", "CALIBER INSIGHTS", "1RM STRENGTH", "DAILY HABITS")
    }

    // Body Map view & sex toggles
    var atlasView by remember { mutableStateOf(BodyMapView.Front) }
    var selectedSex by remember { mutableStateOf(BodyMapSex.Male) }
    var displayMode by remember { mutableStateOf(BodyMapDisplayMode.RecoveryReadiness) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.bodymap_title),
                        fontFamily = FiraCode,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.bodymap_back),
                            tint = colors.legendaryGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.voidBlack,
                    titleContentColor = colors.legendaryGold
                )
            )
        },
        containerColor = colors.voidBlack,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        when (val state = uiState) {
            is BodyMapUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colors.legendaryGold,
                        modifier = Modifier.testTag("bodymap-loading")
                    )
                }
            }

            is BodyMapUiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.bodymap_empty),
                        color = colors.textDim,
                        fontFamily = FiraCode,
                        fontSize = 14.sp
                    )
                }
            }

            is BodyMapUiState.Unmeasured,
            is BodyMapUiState.Success -> {
                val muscles = (state as? BodyMapUiState.Success)?.muscles
                    ?: (state as BodyMapUiState.Unmeasured).muscles

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = AxiomSpacing.m)
                ) {
                    // Canonical 4-Tab Navigation Row
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = colors.voidBlack,
                        contentColor = colors.legendaryGold,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = colors.legendaryGold
                            )
                        },
                        modifier = Modifier.padding(vertical = AxiomSpacing.xs)
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                modifier = Modifier.sizeIn(minHeight = 48.dp),
                                text = {
                                    Text(
                                        text = title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FiraCode
                                    )
                                }
                            )
                        }
                    }

                    // Content for Selected Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (selectedTab) {
                            0 -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(AxiomSpacing.s)
                                ) {
                                    // View & Sex Controls (Front/Back, Male/Female)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AtlasSegmentedToggle(
                                            options = if (isFa) listOf("جلو", "پشت") else listOf("Front", "Back"),
                                            selectedIndex = if (atlasView == BodyMapView.Front) 0 else 1,
                                            onSelected = { index ->
                                                atlasView = if (index == 0) BodyMapView.Front else BodyMapView.Back
                                            },
                                            modifier = Modifier.width(136.dp)
                                        )

                                        AtlasSegmentedToggle(
                                            options = if (isFa) listOf("مرد", "زن") else listOf("Male", "Female"),
                                            selectedIndex = if (selectedSex == BodyMapSex.Male) 0 else 1,
                                            onSelected = { index ->
                                                selectedSex = if (index == 0) BodyMapSex.Male else BodyMapSex.Female
                                            },
                                            modifier = Modifier.width(128.dp)
                                        )
                                    }

                                    // 3D Visual Atlas Canvas
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                    ) {
                                        BodySilhouetteCanvas(
                                            muscles = muscles,
                                            selectedMuscleId = selectedMuscleId,
                                            onSelectMuscle = { viewModel.selectMuscle(it) },
                                            displayMode = displayMode,
                                            sex = selectedSex,
                                            atlasView = atlasView,
                                            onToggleFrontBack = {
                                                atlasView = if (atlasView == BodyMapView.Front) BodyMapView.Back else BodyMapView.Front
                                            }
                                        )
                                    }

                                    // Recovery Timeline Bar
                                    RecoveryTimelineBar(
                                        muscles = muscles,
                                        onMuscleClick = { viewModel.selectMuscle(it.id) },
                                        modifier = Modifier.padding(bottom = AxiomSpacing.s)
                                    )
                                }
                            }

                            1 -> CaliberInsightsView(muscles = muscles)
                            2 -> StrengthOneRMView()
                            3 -> DailyCheckinScreen(onBack = { selectedTab = 0 }, showTopBar = false)
                        }
                    }
                }

                // Muscle Status Bottom Sheet (Training Session Logging)
                selectedMuscleId?.let { id ->
                    muscles.find { it.id == id }?.let { selectedMuscle ->
                        MuscleStatusPanel(
                            selectedMuscle = selectedMuscle,
                            onDismissRequest = { viewModel.selectMuscle(null) },
                            onLogTraining = { mId, hrs, goal, feed, push ->
                                viewModel.logTrainingSession(mId, hrs, goal, feed, push)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AtlasSegmentedToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current

    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(AxiomRadius.m))
            .background(colors.dimSurface)
            .border(
                width = AxiomBorder.hairline,
                color = colors.borderFaint,
                shape = RoundedCornerShape(AxiomRadius.m)
            )
            .padding(2.dp)
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(AxiomRadius.m - 2.dp))
                    .background(if (isSelected) colors.legendaryGold else colors.dimSurface)
                    .selectable(
                        selected = isSelected,
                        role = Role.Tab,
                        onClick = { onSelected(index) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (isSelected) colors.voidBlack else colors.textPrimary,
                    fontFamily = Inter,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
        }
    }
}
