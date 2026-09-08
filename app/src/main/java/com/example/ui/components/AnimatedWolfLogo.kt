package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.CyberBluePrimary
import com.example.ui.theme.CyberBlueSecondary
import com.example.ui.theme.CyberBlueTertiary
import kotlinx.coroutines.launch

@Composable
fun AnimatedWolfLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    showRings: Boolean = true,
    isStartup: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val entranceScale = remember { Animatable(if (isStartup) 0.3f else 1.0f) }
    val tapScale = remember { Animatable(1.0f) }

    // Infinite breathing & subtle head movement animation
    val infiniteTransition = rememberInfiniteTransition(label = "wolf_motion")

    // Subtle breath / pulse scale
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    // Subtle natural head sway tilt (-3.5 deg to +3.5 deg)
    val swayRotation by infiniteTransition.animateFloat(
        initialValue = -3.5f,
        targetValue = 3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )

    // Tech scanning radar ring rotation
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotate"
    )

    // Glowing aura pulse
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura"
    )

    // Trigger startup spring motion
    LaunchedEffect(Unit) {
        if (isStartup) {
            entranceScale.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(
                    dampingRatio = 0.55f,
                    stiffness = 250f
                )
            )
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(entranceScale.value * tapScale.value * breatheScale)
            .rotate(swayRotation)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    tapScale.animateTo(1.18f, spring(dampingRatio = 0.4f, stiffness = 600f))
                    tapScale.animateTo(1.0f, spring(dampingRatio = 0.6f, stiffness = 400f))
                }
                onClick?.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        if (showRings) {
            // Outer glowing ambient ring
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                CyberBlueTertiary.copy(alpha = auraAlpha * 0.35f),
                                CyberBluePrimary.copy(alpha = auraAlpha * 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Tech scan ring with gradient border
            Box(
                modifier = Modifier
                    .size(size * 0.94f)
                    .rotate(ringRotation)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                CyberBlueTertiary,
                                CyberBluePrimary,
                                Color.Transparent,
                                CyberBlueSecondary,
                                CyberBlueTertiary
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        // Inner wolf face image circle
        Box(
            modifier = Modifier
                .size(if (showRings) size * 0.82f else size)
                .clip(CircleShape)
                .background(Color(0xFF03162C))
                .border(
                    width = 2.dp,
                    color = CyberBlueSecondary.copy(alpha = 0.8f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_wolf_logo),
                contentDescription = "Suzuki Wolf Logo",
                modifier = Modifier
                    .size(if (showRings) size * 0.82f else size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}
