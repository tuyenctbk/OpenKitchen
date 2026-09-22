package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textTranslateY = remember { Animatable(30f) }
    val badgeAlpha = remember { Animatable(0f) }

    // Infinite breathing glow transition
    val infiniteTransition = rememberInfiniteTransition(label = "splashPulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val steamFloat by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "steamFloat"
    )

    LaunchedEffect(Unit) {
        // Stage 1: Logo emblem scales up with smooth easing
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
            )
        }
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = LinearEasing)
            )
        }

        // Stage 2: Brand title and tagline slide & fade in
        delay(350)
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
        }
        launch {
            textTranslateY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
        }

        // Stage 3: Status indicator pill fades in
        delay(250)
        launch {
            badgeAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400, easing = LinearEasing)
            )
        }

        // Display linger duration
        delay(1100)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSplashFinished
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF100704),
                        Color(0xFF220F08),
                        Color(0xFF381A0F),
                        Color(0xFF190B06)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Ambient Radial Background Glow
        Box(
            modifier = Modifier
                .size(320.dp)
                .scale(pulseGlow)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x3DFF8A65),
                            Color(0x22FFA726),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Insignia Emblem Container with Floating Steam Effect
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .offset(y = steamFloat.dp)
            ) {
                // Golden Halo Outer Glow Ring
                Surface(
                    color = Color(0x26FFA000),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(136.dp)
                        .border(
                            width = 1.5.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0x00FFB300),
                                    Color(0x80FFB300),
                                    Color(0xFFFFD54F),
                                    Color(0x00FFB300)
                                )
                            ),
                            shape = CircleShape
                        )
                ) {}

                // Inner Emblem
                Surface(
                    color = Color(0xFF2A140B),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(118.dp)
                        .border(
                            width = 2.dp,
                            color = Color(0xFFFFB300).copy(alpha = 0.7f),
                            shape = CircleShape
                        ),
                    shadowElevation = 16.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_splash_logo),
                            contentDescription = stringResource(R.string.splash_logo_desc),
                            modifier = Modifier.size(98.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Typography & Star Badges Group
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = textTranslateY.value.dp)
                    .alpha(textAlpha.value)
            ) {
                // 3 Golden Michelin Stars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Brand Title
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.5.sp,
                    color = Color(0xFFFFFBF7)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Tagline
                Text(
                    text = stringResource(R.string.splash_subtitle),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.8.sp,
                    color = Color(0xFFFFB300).copy(alpha = 0.95f)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Loading Status Pill with Glowing Accent
            Surface(
                color = Color(0xFF1E0E08),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .alpha(badgeAlpha.value)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFFFB300).copy(alpha = 0.35f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                shadowElevation = 6.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = stringResource(R.string.splash_loading),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp,
                        color = Color(0xFFE6D6CE)
                    )
                }
            }
        }
    }
}
