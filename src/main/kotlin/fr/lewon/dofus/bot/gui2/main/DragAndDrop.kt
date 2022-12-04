package fr.lewon.dofus.bot.gui2.main

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

val dragTargetInfo = mutableStateOf(DragTargetInfo())

@Composable
fun PressDraggable(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var globalPosition by remember { mutableStateOf(Offset.Zero) }
    Box(modifier = modifier.fillMaxSize().onGloballyPositioned {
        globalPosition = it.localToWindow(Offset.Zero)
    }) {
        content()
        if (dragTargetInfo.value.isDragging) {
            val draggedItemSize = dragTargetInfo.value.draggedItemSize
            Box(modifier = Modifier.graphicsLayer {
                val offset = (dragTargetInfo.value.dragPosition
                        - globalPosition + dragTargetInfo.value.dragOffset
                        - Offset(draggedItemSize.width / 2f, draggedItemSize.height * 9 / 10f))
                alpha = 0.7f
                translationX = offset.x
                translationY = offset.y
            }.width((draggedItemSize.width * 1.3).dp).height((draggedItemSize.height * 1.3).dp)) {
                dragTargetInfo.value.draggableComposable?.invoke()
            }
        }
    }
}

@Composable
fun <T> DragTarget(
    dataToDrop: T,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    var draggedItemSize by remember { mutableStateOf(IntSize.Zero) }

    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
            draggedItemSize = it.size
        }
        .pointerInput(dataToDrop) {
            detectDragGestures(onDragStart = {
                dragTargetInfo.value = dragTargetInfo.value.copy(
                    dataToDrop = dataToDrop,
                    isDragging = true,
                    dragPosition = currentPosition + it,
                    draggableComposable = content,
                    draggedItemSize = draggedItemSize
                )
            }, onDrag = { change, dragAmount ->
                change.consume()
                dragTargetInfo.value = dragTargetInfo.value.copy(
                    dragOffset = dragTargetInfo.value.dragOffset + Offset(dragAmount.x, dragAmount.y)
                )
            }, onDragEnd = {
                dragTargetInfo.value = dragTargetInfo.value.copy(
                    dragOffset = Offset.Zero,
                    isDragging = false
                )
            }, onDragCancel = {
                dragTargetInfo.value = dragTargetInfo.value.copy(
                    dragOffset = Offset.Zero,
                    isDragging = false
                )
            })
        }) {
        content()
    }
}

@Composable
fun <T> DropTarget(
    modifier: Modifier,
    content: @Composable (BoxScope.(isInBound: Boolean, data: T?) -> Unit)
) {
    val dragInfo = dragTargetInfo.value
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    var isCurrentDropTarget by remember {
        mutableStateOf(false)
    }

    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            isCurrentDropTarget = rect.contains(dragPosition + dragOffset)
        }
    }) {
        val data = if (isCurrentDropTarget && !dragInfo.isDragging) dragInfo.dataToDrop as T? else null
        if (data != null) {
            dragTargetInfo.value = DragTargetInfo()
        }
        content(isCurrentDropTarget, data)
    }
}

data class DragTargetInfo(
    val isDragging: Boolean = false,
    val draggedItemSize: IntSize = IntSize.Zero,
    val dragPosition: Offset = Offset.Zero,
    val dragOffset: Offset = Offset.Zero,
    val draggableComposable: (@Composable () -> Unit)? = null,
    val dataToDrop: Any? = null
)