package fr.lewon.dofus.bot.gui2.custom

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import fr.lewon.dofus.bot.gui2.util.AppColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
        enabled = !refreshing.value && enabled
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
private fun ButtonWithTooltip(
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
        DefaultTooltipArea(title, 20.dp, 1000) {
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
                }
                Box(modifier.fillMaxSize().rotate(angle.value)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun Tooltip(title: String, height: Dp = 35.dp, shape: Shape = CutCornerShape(25, 5, 5, 25)) {
    Button(
        {},
        Modifier.height(height),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
        contentPadding = PaddingValues(start = 10.dp, end = 5.dp),
        shape = shape,
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Text(title, color = Color.White, fontSize = 12.sp)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DefaultTooltipArea(
    title: String,
    tooltipHeight: Dp = 35.dp,
    delayMillis: Int = 0,
    tooltipAlignment: Alignment = Alignment.CenterEnd,
    shape: Shape = CutCornerShape(25, 5, 5, 25),
    content: @Composable () -> Unit
) {
    if (title.isBlank()) {
        Box(Modifier.fillMaxSize()) {
            content()
        }
    } else {
        CustomTooltipArea(
            { Tooltip(title, tooltipHeight, shape) },
            Modifier.fillMaxSize(),
            delayMillis,
            TooltipPlacement.ComponentRect(tooltipAlignment, tooltipAlignment)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomTooltipArea(
    tooltip: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    delayMillis: Int = 500,
    tooltipPlacement: TooltipPlacement = TooltipPlacement.CursorPoint(
        offset = DpOffset(0.dp, 16.dp)
    ),
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var job: Job? by remember { mutableStateOf(null) }
    val isHovered = remember { mutableStateOf(false) }
    var isJobStarted by remember { mutableStateOf(false) }

    fun startShowing() {
        if (!isJobStarted) {
            isJobStarted = true
            job = scope.launch {
                delay(delayMillis.toLong())
                isVisible = true
            }
        }
    }

    fun hide() {
        job?.cancel()
        isJobStarted = false
        isVisible = false
    }

    if (isHovered.value) {
        startShowing()
    } else {
        hide()
    }

    Box(
        modifier = modifier.defaultHoverManager(isHovered)
            .pointerInput(Unit) {
                detectDown {
                    hide()
                }
            }
    ) {
        content()
        if (isVisible) {
            (Popup(
                popupPositionProvider = tooltipPlacement.positionProvider(),
                onDismissRequest = { isVisible = false }
            ) {
                Box {
                    tooltip()
                }
            })
        }
    }
}

private suspend fun PointerInputScope.detectDown(onDown: (Offset) -> Unit) {
    while (true) {
        awaitPointerEventScope {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val down = event.changes.find { it.changedToDown() }
            if (down != null) {
                onDown(down.position)
            }
        }
    }
}