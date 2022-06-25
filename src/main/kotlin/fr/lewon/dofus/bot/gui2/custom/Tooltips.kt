package fr.lewon.dofus.bot.gui2.custom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Tooltip(title: String, height: Dp = 35.dp) {
    Button(
        {},
        Modifier.height(height),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
        contentPadding = PaddingValues(start = 10.dp, end = 5.dp),
        shape = CutCornerShape(25, 5, 5, 25),
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
    content: @Composable () -> Unit
) {
    CustomTooltipArea(
        { Tooltip(title, tooltipHeight) },
        Modifier.fillMaxSize(),
        delayMillis,
        TooltipPlacement.ComponentRect(Alignment.CenterEnd, Alignment.CenterEnd)
    ) {
        content()
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