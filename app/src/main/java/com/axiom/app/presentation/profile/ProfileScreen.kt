package com.axiom.app.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.ui.ProfileUiState
import com.axiom.app.ui.ProfileViewModel
import com.axiom.app.ui.ShareViewModel
import com.axiom.app.ui.components.RankShareCard
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onNavigateToActivation: () -> Unit = {},
    onNavigateToFinancial: () -> Unit = {},
    onNavigateToSkillTree: () -> Unit = {},
    onNavigateToMainQuest: () -> Unit = {},
    onNavigateToAddMission: (skillId: String) -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
    shareViewModel: ShareViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) }
    var showShareDialog by remember { mutableStateOf(false) }
    val graphicsLayer = rememberGraphicsLayer()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAxiomColors.current.voidBlack)
            .testTag("profile_screen_content")
    ) {
        when (val uiState = state) {
            is ProfileUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SystemGreen, modifier = Modifier.testTag("profile_loading_indicator"))
                }
            }
            is ProfileUiState.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = LocalAxiomColors.current.voidBlack,
                        contentColor = SystemGreen,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        listOf("IDENTITY", "STATS", "ARCHIVE").forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMono) }
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        when (selectedTab) {
                            0 -> IdentityTabContent(
                                hunter = uiState.hunter,
                                completedMissions = uiState.completedMissions,
                                activePersona = uiState.activePersona,
                                onEditThesis = onNavigateToMainQuest
                            )
                            1 -> StatsTabContent(
                                hunter = uiState.hunter,
                                skills = uiState.skills,
                                shadowCount = uiState.shadowCount,
                                currentStreak = uiState.currentStreak,
                                longestStreak = uiState.longestStreak,
                                onNavigateToSkillTree = onNavigateToSkillTree
                            )
                            2 -> ArchiveTabContent(
                                completedMissions = uiState.completedMissions,
                                defeatedBosses = uiState.defeatedBosses,
                                weeklyReviews = uiState.weeklyReviews
                            )
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { showShareDialog = true },
                    containerColor = SystemGreen,
                    contentColor = LocalAxiomColors.current.voidBlack,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp)
                        .testTag("profile_share_fab")
                ) {
                    Text("SHARE", style = HudS, modifier = Modifier.padding(horizontal = 12.dp))
                }

                if (showShareDialog) {
                    Dialog(onDismissRequest = { showShareDialog = false }) {
                        Surface(
                            modifier = Modifier.wrapContentSize().border(1.dp, BorderFaint, RoundedCornerShape(8.dp)),
                            color = LocalAxiomColors.current.shadowSurface,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(text = "SHARE REGISTRY PROFILE", color = SystemGreen, style = HudS)
                                
                                RankShareCard(
                                    hunter = uiState.hunter,
                                    missionsComplete = uiState.totalMissionsCompleted,
                                    shadowArmySize = uiState.shadowCount,
                                    dayStreak = uiState.currentStreak,
                                    modifier = Modifier
                                        .drawWithCache {
                                            onDrawWithContent {
                                                graphicsLayer.record { this@onDrawWithContent.drawContent() }
                                                drawContent()
                                            }
                                        }
                                )

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Button(
                                        onClick = { showShareDialog = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = TextSecondary),
                                        modifier = Modifier.weight(1f).border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                                    ) {
                                        Text("CANCEL", style = HudS)
                                    }

                                    Button(
                                        onClick = {
                                            showShareDialog = false
                                            coroutineScope.launch {
                                                try {
                                                    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                                    shareViewModel.shareRankCard(context, bitmap)
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SystemGreen, contentColor = LocalAxiomColors.current.voidBlack),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("SHARE", style = HudS)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
