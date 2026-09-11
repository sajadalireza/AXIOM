package com.axiom.app.presentation.missions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.template.Beachhead
import com.axiom.app.domain.template.MissionTemplate
import com.axiom.app.domain.template.MissionTemplatePack
import com.axiom.app.domain.template.TemplateCategory
import com.axiom.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionTemplateSelectorSheet(
    onDismiss: () -> Unit,
    onSelectTemplate: (MissionTemplate) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<TemplateCategory?>(null) }

    val filteredTemplates = remember(selectedCategory) {
        if (selectedCategory == null) {
            MissionTemplatePack.TEMPLATES
        } else {
            MissionTemplatePack.getByCategory(selectedCategory!!)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = VoidBlack,
        dragHandle = null,
        modifier = Modifier.testTag("mission_template_selector_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SOLOPRENEUR TEMPLATE PACK",
                        color = LegendaryGold,
                        fontFamily = JetBrainsMono,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "High-leverage micro-actions. Zero generic fluff.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // CATEGORY FILTER CHIPS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ALL CHIP
                val isAllSelected = selectedCategory == null
                Box(
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isAllSelected) LegendaryGold.copy(alpha = 0.2f) else VoidBlack)
                        .border(1.dp, if (isAllSelected) LegendaryGold else BorderFaint.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable { selectedCategory = null }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "All (${MissionTemplatePack.TEMPLATES.size})",
                        color = if (isAllSelected) LegendaryGold else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TemplateCategory.entries.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    val count = MissionTemplatePack.getByCategory(cat).size
                    Box(
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) LegendaryGold.copy(alpha = 0.2f) else VoidBlack)
                            .border(1.dp, if (isSelected) LegendaryGold else BorderFaint.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${cat.titleEn} ($count)",
                            color = if (isSelected) LegendaryGold else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TEMPLATE CARDS LIST
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                filteredTemplates.forEach { template ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderFaint.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = ShadowSurface.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Category & Duration Badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = template.category.titleEn.uppercase(),
                                    color = SystemGreen,
                                    fontFamily = JetBrainsMono,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${template.defaultDurationMinutes} MIN • ${template.recommendedTrack}",
                                    color = TextSecondary,
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp
                                )
                            }

                            // Title (EN & FA)
                            Column {
                                Text(
                                    text = template.titleEn,
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = template.titleFa,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            // Concrete Done Condition
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = VoidBlack.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "DONE CONDITION:",
                                        color = LegendaryGold,
                                        fontFamily = JetBrainsMono,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = template.doneConditionEn,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            // Leverage Tip
                            Text(
                                text = "💡 ${template.leverageTipEn}",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )

                            // Select Action Button
                            Button(
                                onClick = { onSelectTemplate(template) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                                    .testTag("btn_select_template_${template.id}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LegendaryGold,
                                    contentColor = VoidBlack
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Use This Template",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
