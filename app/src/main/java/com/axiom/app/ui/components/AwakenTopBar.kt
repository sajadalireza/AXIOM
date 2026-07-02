package com.axiom.app.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.axiom.app.ui.theme.LocalAxiomColors
import com.axiom.app.ui.theme.TitleL

@Composable
fun AwakenTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    rightAction: @Composable (RowScope.() -> Unit)? = null
) {
    val colors = LocalAxiomColors.current
    val accentColor = colors.systemGreen

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
    ) {
        // Translucent blur background using BlurMaskFilter on the background Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Background fill (translucent dark)
            drawRect(
                color = colors.voidBlack.copy(alpha = 0.75f),
                size = size
            )

            // Blur mask filter for background glass/glow effect or accent glow
            drawIntoCanvas { canvas ->
                val paint = Paint().asFrameworkPaint().apply {
                    color = accentColor.copy(alpha = 0.15f).hashCode()
                    maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
                }
                // Draw a subtle soft accent glow at the bottom
                canvas.nativeCanvas.drawRect(
                    0f,
                    height - 10f,
                    width,
                    height + 10f,
                    paint
                )
            }

            // Clean, razor-thin 1dp accent SystemGreen glow line at the bottom
            drawLine(
                color = accentColor,
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Top Bar content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.textPrimary
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = title,
                style = TitleL,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f)
            )

            if (rightAction != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    content = rightAction
                )
            } else {
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}
