package fr.lewon.dofus.bot.gui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.gui.custom.defaultHoverManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val tooltipInfo = mutableStateOf<TooltipInfo?>(null)

@Composable
fun TooltipManageable(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    var globalPosition by remember { mutableStateOf(Offset.Zero) }
    var globalSize by remember { mutableStateOf(IntSize.Zero) }
    Box(modifier = modifier.fillMaxSize().onGloballyPositioned {
        globalPosition = it.localToWindow(Offset.Zero)
        globalSize = it.size
    }) {
        content()
        val currentTooltipInfo = tooltipInfo.value
        if (currentTooltipInfo != null) {
            var tooltipSize by remember { mutableStateOf(IntSize.Zero) }
            var tooltipShape by remember { mutableStateOf(RectangleShape) }
            Box(modifier = Modifier.graphicsLayer {
                if (tooltipSize.width != 0) {
                    val requiredWidth = tooltipSize.width +
                        currentTooltipInfo.elementPosition.x +
                        currentTooltipInfo.elementSize.width
                    val dx: Int
                    if (requiredWidth < globalSize.width) {
                        dx = currentTooltipInfo.elementSize.width
                        tooltipShape = CutCornerShape(8.dp, 2.dp, 2.dp, 8.dp)
                    } else {
                        dx = -tooltipSize.width
                        tooltipShape = CutCornerShape(2.dp, 8.dp, 8.dp, 2.dp)
                    }
                    val offset = currentTooltipInfo.elementPosition - globalPosition + Offset(
                        dx.toFloat(),
                        currentTooltipInfo.tooltipPlacement.computeYOffset(
                            currentTooltipInfo.elementSize.height,
                            tooltipSize.height
                        )
                    )
                    alpha = 1f
                    translationX = offset.x
                    translationY = offset.y
                } else alpha = 0f
            }) {
                Button(
                    {},
                    Modifier.onGloballyPositioned {
                        tooltipSize = it.size
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
                    contentPadding = PaddingValues(start = 10.dp, end = 5.dp),
                    shape = tooltipShape,
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    val tooltipContent = remember(currentTooltipInfo) {
                        movableContentOf {
                            currentTooltipInfo.tooltipContent(this)
                        }
                    }
                    tooltipContent()
                }
            }
        }
    }
}

@Composable
fun TooltipTarget(
    text: String,
    height: Dp = 35.dp,
    delayMillis: Int = 0,
    modifier: Modifier = Modifier,
    tooltipPlacement: TooltipPlacement = TooltipPlacement.Centered,
    content: @Composable BoxScope.() -> Unit,
) {
    val tooltipContent: (@Composable RowScope.() -> Unit)? = if (text.isNotEmpty()) ({
        Row(Modifier.height(height)) {
            Text(
                text,
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
    }) else null
    TooltipTarget(
        key = text,
        tooltipContent = tooltipContent,
        tooltipPlacement = tooltipPlacement,
        delayMillis = delayMillis,
        modifier = modifier,
        content = content
    )
}

@Composable
fun TooltipTarget(
    key: Any,
    tooltipContent: @Composable (RowScope.() -> Unit)?,
    tooltipPlacement: TooltipPlacement = TooltipPlacement.Centered,
    delayMillis: Int = 0,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    var elementPosition by remember { mutableStateOf(Offset.Zero) }
    var elementSize by remember { mutableStateOf(IntSize.Zero) }
    var job: Job? by remember { mutableStateOf(null) }
    var isJobStarted by remember { mutableStateOf(false) }

    val show = {
        if (!isJobStarted) {
            isJobStarted = true
            job = scope.launch {
                delay(delayMillis.toLong())
                tooltipInfo.value = if (tooltipContent != null) {
                    TooltipInfo(
                        tooltipContent = tooltipContent,
                        tooltipPlacement = tooltipPlacement,
                        elementPosition = elementPosition,
                        elementSize = elementSize
                    )
                } else null
            }
        }
    }

    val hide = {
        job?.cancel()
        isJobStarted = false
        tooltipInfo.value = null
    }

    Box(
        modifier = modifier.defaultHoverManager(
            onHover = { show() },
            onExit = { hide() },
            key = key
        ).onGloballyPositioned {
            elementPosition = it.localToWindow(Offset.Zero)
            elementSize = it.size
        }.pointerInput(Unit) {
            detectDown { hide() }
        }) {
        content()
    }
}

suspend fun PointerInputScope.detectDown(onDown: (Offset) -> Unit) {
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

enum class TooltipPlacement(val computeYOffset: (contentHeight: Int, tooltipHeight: Int) -> Float) {
    Centered({ contentHeight, tooltipHeight -> contentHeight / 2f - tooltipHeight / 2f }),
    TopCornerAttached({ contentHeight, _ -> contentHeight / 2f }),
    Top({ _, _ -> 0f }),
}

data class TooltipInfo(
    val tooltipContent: @Composable RowScope.() -> Unit,
    val tooltipPlacement: TooltipPlacement,
    val elementPosition: Offset,
    val elementSize: IntSize,
)