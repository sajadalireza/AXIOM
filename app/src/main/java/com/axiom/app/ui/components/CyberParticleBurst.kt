package com.axiom.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.axiom.app.ui.theme.SystemGreen
import com.axiom.app.ui.theme.LegendaryGold
import kotlinx.coroutines.isActive
import java.util.Random

data class CyberParticle(
    val id: Int,
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    val size: Float,
    val rotationSpeed: Float,
    var rotation: Float = 0f,
    var alpha: Float = 1f,
    var scale: Float = 1f,
    val color: Color,
    val isDiamond: Boolean
)

@Composable
fun CyberParticleBurst(
    trigger: Boolean,
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val density = LocalDensity.current
    val particles = remember { mutableStateListOf<CyberParticle>() }
    val random = remember { Random() }

    // Initialize particles centered on screen/canvas starting point
    LaunchedEffect(trigger) {
        particles.clear()
        val colors = listOf(
            SystemGreen,
            SystemGreen.copy(alpha = 0.8f),
            LegendaryGold,
            SystemGreen.copy(alpha = 0.6f)
        )
        
        // Generate 32 tactical cyber polygonal particles
        for (i in 0 until 32) {
            // Angle in radians
            val angle = random.nextFloat() * 2 * Math.PI.toFloat()
            // High speed burst
            val speed = (2f + random.nextFloat() * 7f) * 12f
            
            particles.add(
                CyberParticle(
                    id = i,
                    x = 0f, // center initialized dynamically in DrawScope
                    y = 0f, 
                    vx = (Math.cos(angle.toDouble()).toFloat() * speed),
                    vy = (Math.sin(angle.toDouble()).toFloat() * speed) - 5f, // offset slightly upward
                    size = (4f + random.nextFloat() * 12f),
                    rotationSpeed = (-10f + random.nextFloat() * 20f) * 2f,
                    color = colors[random.nextInt(colors.size)],
                    isDiamond = random.nextBoolean()
                )
            )
        }

        // Animation update loop utilizing system frame ticks
        val startTime = System.currentTimeMillis()
        val duration = 1800L
        
        while (System.currentTimeMillis() - startTime < duration && coroutineContext.isActive) {
            val elapsed = System.currentTimeMillis() - startTime
            val fraction = elapsed.toFloat() / duration
            
            // Apply physics: gravity, friction & up-drift towards top status bar
            for (i in particles.indices) {
                val p = particles[i]
                
                // Gravity & resistance drag
                val drag = 0.96f
                val newVx = p.vx * drag
                // After initial burst, suck particles slightly upward towards top
                val upwardPull = -1.2f * fraction
                val newVy = (p.vy * drag) + upwardPull
                
                particles[i] = p.copy(
                    x = p.x + newVx,
                    y = p.y + newVy,
                    rotation = p.rotation + p.rotationSpeed,
                    alpha = maxOf(0f, 1f - fraction * 1.1f),
                    scale = maxOf(0f, 1f - fraction * 0.8f)
                )
            }
            
            withFrameNanos { } // yield to next frame
        }
        
        onAnimationComplete()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            // Trigger burst form lower mid area matching mission complete button
            val centerY = size.height * 0.75f 

            particles.forEach { p ->
                val particleX = centerX + p.x
                val particleY = centerY + p.y

                // Skip drawing if invisible
                if (p.alpha <= 0f || p.scale <= 0f) return@forEach

                withTransform({
                    translate(particleX, particleY)
                    rotate(p.rotation)
                    scale(p.scale, p.scale)
                }) {
                    val halfSize = p.size / 2f
                    val path = Path()

                    if (p.isDiamond) {
                        // Drawing futuristic precise tactical diamonds
                        path.moveTo(0f, -p.size)
                        path.lineTo(halfSize, 0f)
                        path.lineTo(0f, p.size)
                        path.lineTo(-halfSize, 0f)
                        path.close()
                    } else {
                        // Drawing custom elegant cyber triangles/polygons
                        path.moveTo(0f, -p.size)
                        path.lineTo(halfSize, halfSize)
                        path.lineTo(-halfSize, halfSize)
                        path.close()
                    }

                    // Fill interior with radiant neon aura
                    drawPath(
                        path = path,
                        color = p.color.copy(alpha = p.alpha * 0.35f)
                    )
                    
                    // Draw outer sharp hologram wireframe
                    drawPath(
                        path = path,
                        color = p.color.copy(alpha = p.alpha),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }
        }
    }
}
