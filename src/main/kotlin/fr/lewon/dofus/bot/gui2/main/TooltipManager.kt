package fr.lewon.dofus.bot.gui2.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import fr.lewon.dofus.bot.gui2.custom.defaultHoverManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val tooltipInfo = mutableStateOf<TooltipInfo?>(null)

@Composable
fun TooltipManageable(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
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
                        tooltipShape = CutCornerShape(25, 5, 5, 25)
                    } else {
                        dx = -tooltipSize.width
                        tooltipShape = CutCornerShape(5, 25, 25, 5)
                    }
                    val offset = currentTooltipInfo.elementPosition - globalPosition + Offset(
                        dx.toFloat(),
                        currentTooltipInfo.elementSize.height / 2f - currentTooltipInfo.tooltipHeight.value / 2
                    )
                    alpha = 1f
                    translationX = offset.x
                    translationY = offset.y
                } else alpha = 0f
            }.height(currentTooltipInfo.tooltipHeight)) {
                Button(
                    {},
                    Modifier.fillMaxHeight().onGloballyPositioned {
                        tooltipSize = it.size
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
                    contentPadding = PaddingValues(start = 10.dp, end = 5.dp),
                    shape = tooltipShape,
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Text(currentTooltipInfo.tooltipText, color = Color.White, fontSize = 12.sp)
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
    content: @Composable BoxScope.() -> Unit
) {
    val scope = rememberCoroutineScope()
    var elementPosition by remember { mutableStateOf(Offset.Zero) }
    var elementSize by remember { mutableStateOf(IntSize.Zero) }
    var job: Job? by remember { mutableStateOf(null) }
    var isJobStarted by remember { mutableStateOf(false) }

    val show = {
        if (!isJobStarted && text.isNotEmpty()) {
            isJobStarted = true
            job = scope.launch {
                delay(delayMillis.toLong())
                tooltipInfo.value = TooltipInfo(text, height, elementPosition, elementSize)
            }
        }
    }

    val hide = {
        job?.cancel()
        isJobStarted = false
        tooltipInfo.value = null
    }

    Box(modifier = modifier.defaultHoverManager(onHover = show, onExit = hide, key = text).onGloballyPositioned {
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

data class TooltipInfo(
    val tooltipText: String,
    val tooltipHeight: Dp,
    val elementPosition: Offset,
    val elementSize: IntSize
)