package com.axiom.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.model.Shadow
import com.axiom.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Extension properties for the Shadow domain model to support gaming attributes
val Shadow.rarity: String
    get() = when (rankLabel.replace("-Rank", "").trim().uppercase()) {
        "S", "ARCHITECT" -> "DEPTH"
        "A", "STRATEGIST" -> "SHIELD"
        "B", "SPECIALIST" -> "CRITICAL"
        "C", "OPERATOR" -> "COMPOUND"
        "D", "BUILDER" -> "FOUNDATION"
        else -> "FOUNDATION"
    }

val Shadow.rarityColor: Long
    get() = when (rarity) {
        "DEPTH" -> 0xFFEF9F27 // LegendaryGold
        "SHIELD" -> 0xFF7F77DD      // EpicPurple
        "CRITICAL" -> 0xFF378ADD      // RareBlue
        "COMPOUND" -> 0xFF188C68
        else -> 0xFF8A8AA0        // CommonGray
    }

val Shadow.level: Int
    get() = when (rarity) {
        "DEPTH" -> 100
        "SHIELD" -> 70
        "CRITICAL" -> 40
        else -> 20
    }

val Shadow.maxLevel: Int
    get() = when (rarity) {
        "DEPTH" -> 100
        "SHIELD" -> 80
        "CRITICAL" -> 50
        else -> 30
    }

val Shadow.powerLevel: Int
    get() = when (rarity) {
        "DEPTH" -> 1000
        "SHIELD" -> 500
        "CRITICAL" -> 300
        "COMPOUND" -> 150
        else -> 100
    }

@Composable
fun ShadowSigil(
    name: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val radius = minOf(w, h) * 0.4f
        val hash = name.hashCode()
        val shapeType = kotlin.math.abs(hash) % 3 // 0 = Triangle, 1 = Hexagon, 2 = Cross
        
        // Draw subtle background radial glow
        drawCircle(
            color = color.copy(alpha = 0.15f),
            radius = radius * 1.1f,
            center = Offset(cx, cy)
        )
        
        when (shapeType) {
            0 -> {
                // Triangle
                val path = Path().apply {
                    moveTo(cx, cy - radius)
                    lineTo(cx + radius * 0.866f, cy + radius * 0.5f)
                    lineTo(cx - radius * 0.866f, cy + radius * 0.5f)
                    close()
                }
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            1 -> {
                // Hexagon
                val path = Path().apply {
                    for (i in 0 until 6) {
                        val angle = i * Math.PI / 3.0
                        val x = cx + radius * Math.cos(angle).toFloat()
                        val y = cy + radius * Math.sin(angle).toFloat()
                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                    }
                    close()
                }
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            else -> {
                // Cross
                val strokeW = 2.dp.toPx()
                // Vertical bar
                drawLine(
                    color = color,
                    start = Offset(cx, cy - radius),
                    end = Offset(cx, cy + radius),
                    strokeWidth = strokeW
                )
                // Horizontal bar
                drawLine(
                    color = color,
                    start = Offset(cx - radius, cy),
                    end = Offset(cx + radius, cy),
                    strokeWidth = strokeW
                )
                // Small inner diamond
                val path = Path().apply {
                    moveTo(cx, cy - radius * 0.3f)
                    lineTo(cx + radius * 0.3f, cy)
                    lineTo(cx, cy + radius * 0.3f)
                    lineTo(cx - radius * 0.3f, cy)
                    close()
                }
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun ShadowCard(
    shadow: Shadow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Entry animation State
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    val alphaAnim by animateFloatAsState(
        targetValue = if (visible) 1.0f else 0.0f,
        animationSpec = tween(500),
        label = "shadow_entry_alpha"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (visible) 1.0f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "shadow_entry_scale"
    )

    val rarity = shadow.rarity
    val rarityColor = Color(shadow.rarityColor.toInt())
    val colors = LocalAxiomColors.current

    val formattedDate = remember(shadow.acquiredAt) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.format(Date(shadow.acquiredAt))
        } catch (e: Exception) {
            "Unknown Date"
        }
    }

    HolographicCard(
        modifier = modifier
            .alpha(alphaAnim)
            .scale(scaleAnim)
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("shadow_card_${shadow.id}"),
        accentColor = rarityColor,
        glowEnabled = rarity == "DEPTH" || rarity == "SHIELD"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sigil Canvas drawing (height 60dp)
            ShadowSigil(
                name = shadow.name,
                color = rarityColor,
                modifier = Modifier
                    .size(60.dp)
                    .testTag("shadow_sigil_${shadow.id}")
            )

            // Shadow Name in TitleM
            Text(
                text = shadow.name.uppercase(),
                style = TitleM,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // "Defeated" Date in LabelS TextDim
            Text(
                text = "Defeated: $formattedDate",
                style = LabelS,
                color = colors.textDim,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Power level: XP rewarded when defeated
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(colors.voidBlack.copy(alpha = 0.5f))
                    .border(width = 0.5.dp, color = rarityColor.copy(alpha = 0.3f), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "⚡ POWER: ${shadow.powerLevel} XP",
                    style = HudSmall,
                    fontSize = 9.sp,
                    color = rarityColor,
                    fontWeight = FontWeight.Bold
                )
            }

            // Small badge showing Category
            Text(
                text = "FOCUS: ${shadow.skillCategory.uppercase()}",
                style = HudSmall,
                fontSize = 8.sp,
                color = colors.systemGreen,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}
