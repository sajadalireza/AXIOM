package com.axiom.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun HomePremiumNudge(
    onUpgradeTap : () -> Unit,
    onDismiss    : () -> Unit,
    modifier     : Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(800); visible = true }

    AnimatedVisibility(
        visible = visible,
        enter   = slideInVertically(initialOffsetY = { 40 }) + fadeIn(tween(400)),
        exit    = fadeOut(tween(300))
    ) {
        Row(
            modifier = modifier.fillMaxWidth()
                .background(ShadowSurface, RoundedCornerShape(4.dp))
                .border(0.5.dp, LegendaryGold.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("🛡", fontSize = 18.sp)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("STREAK SHIELD AVAILABLE",
                    fontFamily = JetBrainsMono, fontSize = 10.sp,
                    color = LegendaryGold, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Text("Protect your streak from accidental breaks.",
                    fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onUpgradeTap, contentPadding = PaddingValues(8.dp, 2.dp)) {
                    Text("VIEW →", fontFamily = JetBrainsMono, fontSize = 10.sp,
                        color = LegendaryGold, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onDismiss, contentPadding = PaddingValues(8.dp, 2.dp)) {
                    Text("✕", fontFamily = JetBrainsMono, fontSize = 10.sp, color = TextDim)
                }
            }
        }
    }
}
