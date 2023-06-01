package fr.lewon.dofus.bot.gui.custom

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.gui.util.AppColors


@Composable
fun RefreshButton(
    onClick: () -> Unit,
    tooltipTitle: String,
    shape: Shape,
    hoverBackgroundColor: Color = AppColors.backgroundColor,
    defaultBackgroundColor: Color = AppColors.backgroundColor,
    refreshingBackgroundColor: Color = AppColors.VERY_DARK_BG_COLOR,
    hoverAnimation: Boolean = true,
    width: Dp = 30.dp,
    imageModifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconColor: Color = Color.White,
    isHovered: MutableState<Boolean> = remember { mutableStateOf(false) },
    refreshing: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    val angle = animateFloatAsState(
        targetValue = if (refreshing.value) Int.MAX_VALUE.toFloat() else 0f,
        animationSpec = tween(
            durationMillis = if (refreshing.value) Int.MAX_VALUE else 0,
            easing = LinearEasing
        )
    )
    ButtonWithTooltip(
        onClick = {
            Thread {
                try {
                    refreshing.value = true
                    onClick()
                } finally {
                    refreshing.value = false
                }
            }.start()
        },
        title = tooltipTitle,
        imageVector = Icons.Default.Refresh,
        shape = shape,
        hoverBackgroundColor = if (refreshing.value) refreshingBackgroundColor else hoverBackgroundColor,
        defaultBackgroundColor = if (refreshing.value) refreshingBackgroundColor else defaultBackgroundColor,
        hoverAnimation = hoverAnimation,
        width = width,
        imageModifier = imageModifier.rotate(angle.value),
        enabled = !refreshing.value && enabled,
        isHovered = isHovered,
        iconColor = iconColor
    )
}

@Composable
fun ButtonWithTooltip(
    onClick: () -> Unit,
    title: String,
    imageVector: ImageVector,
    shape: Shape,
    hoverBackgroundColor: Color = AppColors.backgroundColor,
    defaultBackgroundColor: Color = AppColors.backgroundColor,
    hoverAnimation: Boolean = true,
    iconAlignment: Alignment = Alignment.Center,
    width: Dp = 30.dp,
    imageModifier: Modifier = Modifier,
    enabled: Boolean = true,
    isHovered: MutableState<Boolean> = remember { mutableStateOf(false) },
    iconColor: Color = Color.White
) {
    ButtonWithTooltip(
        onClick,
        title,
        shape,
        hoverBackgroundColor,
        defaultBackgroundColor,
        hoverAnimation,
        width,
        enabled,
        isHovered
    ) {
        Box(Modifier.fillMaxSize()) {
            Image(
                imageVector,
                "",
                imageModifier.fillMaxSize(0.95f).align(iconAlignment),
                colorFilter = ColorFilter.tint(iconColor),
            )
        }
    }
}

@Composable
fun ButtonWithTooltip(
    onClick: () -> Unit,
    title: String,
    imagePainter: Painter,
    shape: Shape,
    hoverBackgroundColor: Color = AppColors.backgroundColor,
    defaultBackgroundColor: Color = AppColors.backgroundColor,
    hoverAnimation: Boolean = false,
    iconAlignment: Alignment = Alignment.Center,
    width: Dp = 30.dp,
    imageModifier: Modifier = Modifier,
    enabled: Boolean = true,
    isHovered: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    ButtonWithTooltip(
        onClick,
        title,
        shape,
        hoverBackgroundColor,
        defaultBackgroundColor,
        hoverAnimation,
        width,
        enabled,
        isHovered
    ) {
        Box(Modifier.fillMaxSize()) {
            Image(
                imagePainter,
                "",
                imageModifier.fillMaxSize(0.95f).align(iconAlignment),
            )
        }
    }
}

@Composable
fun ButtonWithTooltip(
    onClick: () -> Unit,
    title: String,
    shape: Shape,
    hoverBackgroundColor: Color = AppColors.backgroundColor,
    defaultBackgroundColor: Color = AppColors.backgroundColor,
    hoverAnimation: Boolean = false,
    width: Dp = 30.dp,
    enabled: Boolean = true,
    isHovered: MutableState<Boolean> = remember { mutableStateOf(false) },
    content: @Composable () -> Unit
) {
    val angle = animateFloatAsState(
        targetValue = if (hoverAnimation && isHovered.value && enabled) 360f else 0f,
        animationSpec = tween(
            durationMillis = if (hoverAnimation && isHovered.value && enabled) 250 else 0,
            easing = LinearEasing
        )
    )
    Row(Modifier.width(width).defaultHoverManager(isHovered)) {
        TooltipTarget(title, 20.dp, 1000, modifier = Modifier.fillMaxSize()) {
            Button(
                onClick,
                shape = shape,
                colors = ButtonDefaults.buttonColors(backgroundColor = defaultBackgroundColor),
                contentPadding = PaddingValues(0.dp),
                enabled = enabled
            ) {
                var modifier = if (enabled) Modifier.handPointerIcon() else Modifier
                if (enabled && isHovered.value) {
                    modifier = modifier.background(hoverBackgroundColor)
                } else if (!isHovered.value || !enabled) {
                    modifier = modifier.background(defaultBackgroundColor)
                }
                Box(modifier.fillMaxSize().rotate(angle.value)) {
                    content()
                }
            }
        }
    }
}

