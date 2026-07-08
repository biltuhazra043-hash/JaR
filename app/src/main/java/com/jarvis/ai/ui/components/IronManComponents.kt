package com.jarvis.ai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jarvis.ai.ui.theme.JarvisColors

/**
 * Arc Reactor animation - the signature Iron Man power core.
 */
@Composable
fun ArcReactorAnimation(
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    isActive: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "arcReactor")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = if (isActive) 0.6f else 0.3f,
        targetValue = if (isActive) 1.0f else 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow
        Canvas(
            modifier = Modifier
                .size(size * 1.4f)
                .scale(pulseScale)
                .blur(if (isActive) 40.dp else 20.dp)
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        JarvisColors.BlueGlow.copy(alpha = glowAlpha * 0.5f),
                        JarvisColors.BlueGlow.copy(alpha = 0f)
                    )
                ),
                radius = this.size.minDimension / 2
            )
        }

        // Outer ring
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2, this.size.height / 2)
            val outerRadius = this.size.minDimension / 2 - 8f

            // Outer ring
            drawCircle(
                color = JarvisColors.BlueGlow.copy(alpha = 0.3f),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 3f)
            )

            // Middle ring with rotation
            drawArc(
                color = JarvisColors.ArcReactor,
                startAngle = rotation,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(center.x - outerRadius + 20f, center.y - outerRadius + 20f),
                size = androidx.compose.ui.geometry.Size(
                    (outerRadius - 20f) * 2,
                    (outerRadius - 20f) * 2
                ),
                style = Stroke(width = 2f, cap = StrokeCap.Round)
            )

            // Inner ring
            drawCircle(
                color = JarvisColors.NeonBlue.copy(alpha = 0.5f),
                radius = outerRadius - 40f,
                center = center,
                style = Stroke(width = 1.5f)
            )

            // Core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        JarvisColors.ArcReactor,
                        JarvisColors.BlueGlow,
                        JarvisColors.BlueGlow.copy(alpha = 0f)
                    ),
                    center = center,
                    radius = outerRadius - 60f
                ),
                radius = outerRadius - 60f,
                center = center
            )

            // Triangle segments
            for (i in 0 until 3) {
                val angle = (rotation * 2 + i * 120f) * (Math.PI / 180f).toFloat()
                val startX = center.x + (outerRadius - 50f) * kotlin.math.cos(angle)
                val startY = center.y + (outerRadius - 50f) * kotlin.math.sin(angle)
                val endX = center.x + (outerRadius - 30f) * kotlin.math.cos(angle + 0.2f)
                val endY = center.y + (outerRadius - 30f) * kotlin.math.sin(angle + 0.2f)

                drawLine(
                    color = JarvisColors.ArcReactor.copy(alpha = 0.7f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * Iron Man Mask with glowing eyes.
 */
@Composable
fun IronManMask(
    modifier: Modifier = Modifier,
    isListening: Boolean = false,
    isSpeaking: Boolean = false,
    size: Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mask")

    val eyeBrightness by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = if (isListening) 1.0f else if (isSpeaking) 0.8f else 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 800 else 2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eyeBrightness"
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Mask outline
        drawRoundRect(
            color = JarvisColors.Gunmetal,
            size = androidx.compose.ui.geometry.Size(w * 0.8f, h * 0.85f),
            topLeft = Offset(w * 0.1f, h * 0.075f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.2f, w * 0.3f)
        )

        // Left eye
        val leftEyeCenter = Offset(w * 0.35f, h * 0.4f)
        drawOval(
            color = JarvisColors.BlueGlow.copy(alpha = eyeBrightness),
            topLeft = Offset(leftEyeCenter.x - w * 0.08f, leftEyeCenter.y - h * 0.04f),
            size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.08f)
        )

        // Right eye
        val rightEyeCenter = Offset(w * 0.65f, h * 0.4f)
        drawOval(
            color = JarvisColors.BlueGlow.copy(alpha = eyeBrightness),
            topLeft = Offset(rightEyeCenter.x - w * 0.08f, rightEyeCenter.y - h * 0.04f),
            size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.08f)
        )

        // Mouth plate
        drawRoundRect(
            color = JarvisColors.GunmetalLight,
            size = androidx.compose.ui.geometry.Size(w * 0.4f, h * 0.06f),
            topLeft = Offset(w * 0.3f, h * 0.65f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
        )

        // Center line
        drawLine(
            color = JarvisColors.RedDark.copy(alpha = 0.5f),
            start = Offset(w * 0.5f, h * 0.15f),
            end = Offset(w * 0.5f, h * 0.6f),
            strokeWidth = 2f
        )

        // Forehead detail
        drawLine(
            color = JarvisColors.Red.copy(alpha = 0.3f),
            start = Offset(w * 0.3f, h * 0.2f),
            end = Offset(w * 0.5f, h * 0.15f),
            strokeWidth = 1.5f
        )
        drawLine(
            color = JarvisColors.Red.copy(alpha = 0.3f),
            start = Offset(w * 0.7f, h * 0.2f),
            end = Offset(w * 0.5f, h * 0.15f),
            strokeWidth = 1.5f
        )
    }
}

/**
 * Voice waveform visualization.
 */
@Composable
fun VoiceWaveform(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    barCount: Int = 20,
    color: Color = JarvisColors.BlueGlow
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val bars = List(barCount) { index ->
        val phase = index * (360f / barCount)
        val height by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = if (isActive) {
                when (index % 3) {
                    0 -> 0.9f
                    1 -> 0.6f
                    else -> 0.8f
                }
            } else 0.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = if (isActive) 600 + index * 50 else 2000,
                    easing = EaseInOutSine
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
        height
    }

    Canvas(modifier = modifier.height(48.dp).fillMaxWidth()) {
        val barWidth = size.width / (barCount * 2)
        val maxHeight = size.height

        bars.forEachIndexed { index, heightFraction ->
            val x = index * barWidth * 2
            val barHeight = maxHeight * heightFraction

            drawRoundRect(
                color = color.copy(alpha = 0.7f + heightFraction * 0.3f),
                topLeft = Offset(x, (maxHeight - barHeight) / 2),
                size = androidx.compose.ui.geometry.Size(barWidth * 0.8f, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )
        }
    }
}

/**
 * Glassmorphic card container.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        JarvisColors.GlassSurface,
                        JarvisColors.GlassBg
                    )
                ),
                shape = MaterialTheme.shapes.large
            )
    ) {
        content()
    }
}

/**
 * Status indicator dot with pulse animation.
 */
@Composable
fun StatusDot(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    activeColor: Color = JarvisColors.Success,
    size: Dp = 8.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "statusDot")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .size(size * (if (isActive) scale else 1f))
            .clip(CircleShape)
            .background(
                if (isActive) activeColor else JarvisColors.TextTertiary
            )
    )
}
