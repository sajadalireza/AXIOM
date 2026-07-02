package com.axiom.app.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.data.local.entity.WeeklyReviewEntity
import com.axiom.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ArchiveTabContent(
    completedMissions: List<Mission>,
    defeatedBosses: List<Dungeon>,
    weeklyReviews: List<WeeklyReviewEntity>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    var currentPage by remember { mutableStateOf(0) }
    val pageSize = 5
    val totalPages = (completedMissions.size + pageSize - 1) / pageSize

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // SECTION 1: Defeated Bosses / Checkpoints
        Text(
            text = "DEFEATED BOSSES",
            style = HudM,
            color = SystemGreen,
            letterSpacing = 1.sp
        )

        if (defeatedBosses.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
                border = BorderStroke(1.dp, BorderFaint)
            ) {
                Text(
                    text = "No boss dungeons cleared yet. Defeat bosses to unlock legend entries.",
                    style = LabelS,
                    color = TextDim,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            defeatedBosses.forEach { boss ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
                    border = BorderStroke(1.dp, SystemGreen.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(boss.name, style = LabelL, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("DEFEATED", style = HudS, color = SystemGreen)
                        }
                        Text(boss.description, style = LabelS, color = TextSecondary)
                        boss.completedAt?.let {
                            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(it))
                            Text("Cleared on: $dateStr", style = LabelS, color = TextDim)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SECTION 2: Completed Missions (with pagination)
        Text(
            text = "COMPLETED MISSIONS",
            style = HudM,
            color = SystemGreen,
            letterSpacing = 1.sp
        )

        if (completedMissions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
                border = BorderStroke(1.dp, BorderFaint)
            ) {
                Text(
                    text = "No completed missions found.",
                    style = LabelS,
                    color = TextDim,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val startIndex = currentPage * pageSize
            val endIndex = (startIndex + pageSize).coerceAtMost(completedMissions.size)
            val pagedMissions = completedMissions.subList(startIndex, endIndex)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                pagedMissions.forEach { mission ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
                        border = BorderStroke(1.dp, BorderFaint)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(mission.title, style = LabelL, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("+${mission.xpReward} XP", style = HudS, color = SystemGreen)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("TRACK: ${mission.track.uppercase()}", style = LabelS, color = TextDim)
                                mission.completedAt?.let {
                                    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(it))
                                    Text(dateStr, style = LabelS, color = TextDim)
                                }
                            }
                        }
                    }
                }

                // Pagination Controls
                if (totalPages > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { if (currentPage > 0) currentPage-- },
                            enabled = currentPage > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = colors.shadowSurface, contentColor = SystemGreen)
                        ) {
                            Text("PREV", style = HudS)
                        }

                        Text("PAGE ${currentPage + 1} OF $totalPages", style = HudS, color = TextPrimary)

                        Button(
                            onClick = { if (currentPage < totalPages - 1) currentPage++ },
                            enabled = currentPage < totalPages - 1,
                            colors = ButtonDefaults.buttonColors(containerColor = colors.shadowSurface, contentColor = SystemGreen)
                        ) {
                            Text("NEXT", style = HudS)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SECTION 3: Weekly Reviews
        Text(
            text = "WEEKLY REVIEWS",
            style = HudM,
            color = SystemGreen,
            letterSpacing = 1.sp
        )

        if (weeklyReviews.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
                border = BorderStroke(1.dp, BorderFaint)
            ) {
                Text(
                    text = "No weekly reviews completed yet.",
                    style = LabelS,
                    color = TextDim,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            weeklyReviews.forEach { review ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
                    border = BorderStroke(1.dp, BorderFaint)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val reviewDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(review.timestamp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Review Week", style = LabelL, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text(reviewDate, style = HudS, color = SystemGreen)
                        }

                        Text("JOURNAL RECORD:", style = LabelS, color = SystemGreen)
                        Text(review.step5JournalText.ifEmpty { "None" }, style = LabelS, color = TextSecondary)

                        Text("CRITIC FEEDBACK:", style = LabelS, color = PenaltyRed)
                        Text(review.step3CriticFeedback.ifEmpty { "None" }, style = LabelS, color = TextSecondary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
