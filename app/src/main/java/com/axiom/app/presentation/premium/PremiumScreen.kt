package com.axiom.app.presentation.premium

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.R
import com.axiom.app.ui.PremiumViewModel
import com.axiom.app.ui.theme.*

@Composable
fun PremiumScreen(
    onBack: () -> Unit,
    viewModel: PremiumViewModel = hiltViewModel()
) {
    val isPremiumState by viewModel.isPremium.collectAsStateWithLifecycle()
    val activePlanState by viewModel.activePlan.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val colors = LocalAxiomColors.current
    
    // Available simulated plans
    val plans = remember {
        listOf(
            PremiumPlanData(
                id = "monthly_pass",
                nameRes = R.string.plan_monthly_name,
                priceRes = R.string.plan_monthly_price,
                descRes = R.string.plan_monthly_desc,
                badge = null
            ),
            PremiumPlanData(
                id = "yearly_pass",
                nameRes = R.string.plan_yearly_name,
                priceRes = R.string.plan_yearly_price,
                descRes = R.string.plan_yearly_desc,
                badge = "POPULAR / ویژه"
            ),
            PremiumPlanData(
                id = "lifetime_premium",
                nameRes = R.string.plan_lifetime_name,
                priceRes = R.string.plan_lifetime_price,
                descRes = R.string.plan_lifetime_desc,
                badge = "BEST VALUE / برتر"
            )
        )
    }

    var selectedPlan by remember { mutableStateOf(plans[1]) } // Annual selected by default
    var showBazaarPaymentDialog by remember { mutableStateOf(false) }
    var showSuccessCelebration by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(colors.voidBlack)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "AXIOM",
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = colors.textDim,
                letterSpacing = 3.sp
            )
            Text(
                text = "PREMIUM\nPROTOCOL",
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = LegendaryGold,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp
            )
            Text(
                text = stringResource(id = R.string.premium_arm_yours),
                fontFamily = Inter,
                fontSize = 14.sp,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))

            val benefits = listOf(
                Triple("🛡", stringResource(id = R.string.premium_benefit_1_title), stringResource(id = R.string.premium_benefit_1_desc)),
                Triple("⚡", stringResource(id = R.string.premium_benefit_2_title), stringResource(id = R.string.premium_benefit_2_desc)),
                Triple("◈", stringResource(id = R.string.premium_benefit_3_title), stringResource(id = R.string.premium_benefit_3_desc))
            )
            benefits.forEach { (glyph, title, desc) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.shadowSurface, RoundedCornerShape(4.dp))
                        .border(0.5.dp, colors.borderFaint, RoundedCornerShape(4.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment     = Alignment.Top
                ) {
                    Text(glyph, fontFamily = JetBrainsMono, fontSize = 22.sp, color = LegendaryGold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = title,
                            fontFamily = JetBrainsMono,
                            fontSize = 13.sp,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = desc,
                            fontFamily = Inter,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (isPremiumState) {
                // Determine active plan title
                val activePlanTitle = when (activePlanState) {
                    "monthly_pass" -> stringResource(id = R.string.plan_monthly_name)
                    "yearly_pass" -> stringResource(id = R.string.plan_yearly_name)
                    "lifetime_premium" -> stringResource(id = R.string.plan_lifetime_name)
                    else -> "PREMIUM CORE PROTOCOL"
                }

                // Already Premium display
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.systemGreen.copy(alpha = 0.10f), RoundedCornerShape(6.dp))
                        .border(1.dp, colors.systemGreen.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Active",
                        tint = colors.systemGreen,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.premium_confirmed_title),
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = colors.systemGreen,
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .background(colors.systemGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.premium_active_protocol, activePlanTitle.uppercase()),
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.systemGreen
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.premium_unlocked_info),
                        fontFamily = Inter,
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                    
                    Spacer(Modifier.height(4.dp))
                    TextButton(onClick = { viewModel.downgradePremium() }) {
                        Text(
                            text = stringResource(id = R.string.premium_emergency_disable),
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            color = colors.penaltyRed
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(id = R.string.premium_choose_uplink),
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LegendaryGold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left
                )

                // Render plan card options
                plans.forEach { plan ->
                    val isSelected = selectedPlan.id == plan.id
                    val planBorderColor = if (isSelected) LegendaryGold else colors.borderFaint
                    val planBgColor = if (isSelected) colors.shadowSurface else Color.Transparent

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(planBgColor, RoundedCornerShape(8.dp))
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = planBorderColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedPlan = plan }
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = plan.nameRes),
                                fontFamily = JetBrainsMono,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) LegendaryGold else colors.textPrimary
                            )

                            if (plan.badge != null) {
                                Box(
                                    modifier = Modifier
                                        .background(LegendaryGold, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = plan.badge,
                                        fontFamily = JetBrainsMono,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = colors.voidBlack
                                    )
                                }
                            }
                        }

                        Text(
                            text = stringResource(id = plan.priceRes),
                            fontFamily = JetBrainsMono,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) LegendaryGold else colors.textSecondary
                        )

                        Text(
                            text = stringResource(id = plan.descRes),
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = colors.textDim,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Subscription action button
                Button(
                    onClick = { showBazaarPaymentDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_upgrade_premium"),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = LegendaryGold,
                        contentColor = colors.voidBlack
                    ),
                    shape    = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.premium_unlock_plan, stringResource(id = selectedPlan.nameRes).uppercase()),
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                Text(
                    text = stringResource(id = R.string.premium_billed_securely),
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    color = colors.textDim
                )
            }

            TextButton(onClick = onBack) {
                Text(stringResource(id = R.string.premium_back_btn), fontFamily = JetBrainsMono, fontSize = 11.sp, color = colors.textDim)
            }
            Spacer(Modifier.height(40.dp))
        }

        // Bazaar Checkout Dialog Simulation
        if (showBazaarPaymentDialog) {
            Dialog(onDismissRequest = { showBazaarPaymentDialog = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E1E24))
                        .border(1.dp, Color(0xFF3A3A42), RoundedCornerShape(8.dp))
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Bazaar Branding Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.premium_bazaar_title),
                                fontFamily = JetBrainsMono,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                            Text(
                                text = stringResource(id = R.string.premium_billing_portal),
                                fontFamily = Inter,
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        }

                        Divider(color = Color(0xFF3A3A42), thickness = 1.dp)

                        // Product Billing Info Box
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF26262F), RoundedCornerShape(4.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${stringResource(id = R.string.premium_product_name)} (${stringResource(id = selectedPlan.nameRes)})",
                                fontFamily = Inter,
                                fontSize = 12.sp,
                                color = colors.textPrimary
                            )
                            Text(
                                text = stringResource(id = R.string.premium_developer_label),
                                fontFamily = Inter,
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(id = R.string.premium_price_label),
                                    fontFamily = Inter,
                                    fontSize = 11.sp,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = stringResource(id = selectedPlan.priceRes),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LegendaryGold
                                )
                            }
                        }

                        // Simulation Note
                        Text(
                            text = stringResource(id = R.string.premium_simulation_note),
                            fontFamily = Inter,
                            fontSize = 10.sp,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )

                        // Checkout Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showBazaarPaymentDialog = false },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, colors.borderFaint),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textSecondary)
                            ) {
                                Text(stringResource(id = R.string.premium_cancel_btn), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    showBazaarPaymentDialog = false
                                    viewModel.purchasePremium(selectedPlan.id)
                                    showSuccessCelebration = true
                                },
                                modifier = Modifier.weight(1.5f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = colors.textPrimary)
                            ) {
                                Text(stringResource(id = R.string.premium_pay_btn), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Success Celebration Dialog
        if (showSuccessCelebration) {
            Dialog(onDismissRequest = { showSuccessCelebration = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.voidBlack)
                        .border(1.dp, LegendaryGold, RoundedCornerShape(8.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("👑", fontSize = 48.sp)
                        Text(
                            text = stringResource(id = R.string.premium_activated_title),
                            fontFamily = JetBrainsMono,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LegendaryGold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(id = R.string.premium_activated_desc, stringResource(id = selectedPlan.nameRes)),
                            fontFamily = Inter,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Button(
                            onClick = { showSuccessCelebration = false },
                            colors   = ButtonDefaults.buttonColors(containerColor = LegendaryGold, contentColor = colors.voidBlack)
                        ) {
                            Text(stringResource(id = R.string.premium_acknowledged), fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// Data class representation for UI Plan Selection
data class PremiumPlanData(
    val id: String,
    val nameRes: Int,
    val priceRes: Int,
    val descRes: Int,
    val badge: String?
)
