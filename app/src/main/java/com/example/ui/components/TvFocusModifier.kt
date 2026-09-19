package com.example.ui.components

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

val LocalIsTvDevice = compositionLocalOf { false }

fun Context.isTvDevice(): Boolean {
    val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
    if (uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
        return true
    }
    return packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
}

/**
 * Android TV 1st class focus modifier with premium 10-ft UI design.
 * On Android TV:
 * - Natural spring bounce (1.06x scale)
 * - Luminous multi-layered focus outline with glowing aura and elevation
 * - D-Pad Center / Enter key event listener ensuring immediate trigger on TV remotes
 *
 * On Mobile / Tablet:
 * - Strictly omits focus rectangles, borders, scale jumps, and elevation popups.
 * - Standard smooth touch interaction.
 */
@Composable
fun Modifier.tvFocusable(
    tag: String,
    shape: Shape = RoundedCornerShape(16.dp),
    focusedScale: Float = 1.05f,
    focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
    focusedBorderWidth: Dp = 3.dp,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
    unfocusedBorderWidth: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null
): Modifier {
    val context = LocalContext.current
    val isTv = LocalIsTvDevice.current || context.isTvDevice()

    // For Mobile & Tablet: strictly omit focused indicator rectangle / border / scale effect and extra clickable
    if (!isTv) {
        return this.testTag(tag)
    }

    // Android TV: D-Pad navigable with illuminated focus border & scale bounce
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        onFocusChanged?.invoke(isFocused)
    }

    // Organic tactile spring animation
    val scale by animateFloatAsState(
        targetValue = if (isFocused) focusedScale else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = 320f
        ),
        label = "tvScale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isFocused) 16.dp else 1.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "tvShadow"
    )

    // Luminous halo border brush for focused state
    val haloBrush = Brush.linearGradient(
        colors = listOf(
            focusedBorderColor,
            Color(0xFFE8DEF8),
            focusedBorderColor
        )
    )

    return this
        .testTag(tag)
        .zIndex(if (isFocused) 15f else 1f)
        .scale(scale)
        .shadow(
            elevation = shadowElevation,
            shape = shape,
            clip = false,
            ambientColor = if (isFocused) focusedBorderColor.copy(alpha = 0.45f) else Color.Transparent,
            spotColor = if (isFocused) focusedBorderColor.copy(alpha = 0.65f) else Color.Transparent
        )
        .then(
            if (isFocused) {
                Modifier
                    .border(focusedBorderWidth, haloBrush, shape)
                    .border(focusedBorderWidth + 2.dp, focusedBorderColor.copy(alpha = 0.25f), shape)
            } else if (unfocusedBorderWidth > 0.dp) {
                Modifier.border(unfocusedBorderWidth, unfocusedBorderColor, shape)
            } else {
                Modifier
            }
        )
        .focusable(interactionSource = interactionSource)
        .onKeyEvent { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyUp &&
                (keyEvent.key == Key.DirectionCenter || keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter)
            ) {
                if (onClick != null) {
                    onClick.invoke()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
            } else {
                Modifier
            }
        )
}
