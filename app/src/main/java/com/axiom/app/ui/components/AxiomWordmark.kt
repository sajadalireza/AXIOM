package com.axiom.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.Outfit

/**
 * Exact AXIOM cinematic wordmark:
 * Glowing cyan/emerald chevron "Λ" followed by spaced "X I O M".
 */
@Composable
fun AxiomWordmarkHeader(
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 20.sp,
    textColor: Color = Color(0xFFF0F4F2)
) {
    Row(
        modifier = modifier.semantics {
            contentDescription = "AXIOM"
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Λ",
            fontFamily = Outfit,
            fontWeight = FontWeight.Normal,
            fontSize = fontSize,
            color = Color(0xFF2EE6A8),
            letterSpacing = 6.sp
        )
        Text(
            text = " X I O M",
            fontFamily = Outfit,
            fontWeight = FontWeight.Light,
            fontSize = fontSize,
            color = textColor,
            letterSpacing = 6.sp
        )
    }
}

/**
 * Exact cinematic glowing pill button matching PO references:
 * - 56dp height (exceeds 48dp WCAG touch target)
 * - Rounded pill shape (32dp)
 * - Emerald glass gradient background with subtle translucent sheen
 * - 1.2dp emerald/gold border
 */
@Composable
fun AxiomPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = Color(0xFF2EE6A8),
    arrowIcon: Boolean = true,
    minHeight: Dp = 56.dp,
) {
    val shape = RoundedCornerShape(32.dp)
    val backgroundBrush = if (enabled) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF093125).copy(alpha = 0.85f),
                Color(0xFF031812).copy(alpha = 0.95f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF141F1B).copy(alpha = 0.6f),
                Color(0xFF0C1411).copy(alpha = 0.8f)
            )
        )
    }

    val currentBorderColor = if (enabled) borderColor else Color(0x334E655C)
    val textColor = if (enabled) Color(0xFFF0F7F4) else Color(0xFF6B7E76)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .clip(shape)
            .background(backgroundBrush)
            .border(BorderStroke(1.2.dp, currentBorderColor), shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                fontFamily = Outfit,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                color = textColor,
                letterSpacing = 0.5.sp
            )
            if (arrowIcon) {
                Text(
                    text = "  →",
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = textColor
                )
            }
        }
    }
}
