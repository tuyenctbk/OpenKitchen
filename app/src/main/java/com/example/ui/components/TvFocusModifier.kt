package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import androidx.compose.ui.zIndex

/**
 * Android TV 1st class focus modifier.
 * Provides distinct scale bounce (1.06x), high-contrast illuminated border,
 * elevation, and z-index lift when focused by TV remote D-Pad.
 */
@Composable
fun Modifier.tvFocusable(
    tag: String,
    shape: Shape = RoundedCornerShape(16.dp),
    focusedScale: Float = 1.06f,
    focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
    focusedBorderWidth: Dp = 3.5.dp,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
    unfocusedBorderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    androidx.compose.runtime.LaunchedEffect(isFocused) {
        onFocusChanged?.invoke(isFocused)
    }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) focusedScale else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "tvScale"
    )

    val shadowElevation = if (isFocused) 18.dp else 2.dp

    return this
        .testTag(tag)
        .zIndex(if (isFocused) 20f else 1f)
        .scale(scale)
        .shadow(shadowElevation, shape, clip = false)
        .then(
            if (isFocused) {
                Modifier
                    .border(focusedBorderWidth, focusedBorderColor, shape)
                    .border(focusedBorderWidth + 2.dp, focusedBorderColor.copy(alpha = 0.35f), shape)
            } else if (unfocusedBorderWidth > 0.dp) {
                Modifier.border(unfocusedBorderWidth, unfocusedBorderColor, shape)
            } else {
                Modifier
            }
        )
        .focusable(interactionSource = interactionSource)
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
