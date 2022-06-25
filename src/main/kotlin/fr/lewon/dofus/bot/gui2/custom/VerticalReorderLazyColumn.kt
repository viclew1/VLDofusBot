package fr.lewon.dofus.bot.gui2.custom

import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.util.io.WaitUtil
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

@Composable
fun <T> VerticalReorderLazyColumn(
    onMove: (item: T, toIndex: Int) -> Unit,
    items: MutableState<List<T>>,
    itemHeight: Int,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    isDragging: MutableState<Boolean> = mutableStateOf(false),
    itemContent: @Composable (T) -> Unit
) {
    var draggedItem by remember { mutableStateOf<T?>(null) }
    var draggedItemOffset by remember { mutableStateOf(0f) }
    var draggedItemIndex by remember { mutableStateOf(0) }
    val realDraggedItemOffset = getRealDraggedItemOffset(
        draggedItemIndex, draggedItemOffset, items.value.size, itemHeight
    )
    var draggingStarted by remember { mutableStateOf(false) }
    val draggedItemDIndex =
        (realDraggedItemOffset.toInt() + draggedItemOffset.sign.toInt() * itemHeight / 2) / itemHeight
    LazyColumn(state = lazyListState, modifier = modifier) {
        itemsIndexed(items.value) { index, item ->
            Column(
                Modifier.height(itemHeight.dp).lineOffset(
                    draggedItem,
                    realDraggedItemOffset,
                    draggedItemIndex,
                    draggedItemDIndex,
                    item,
                    index,
                    itemHeight
                ).draggable(
                    rememberDraggableState {
                        draggedItemOffset += it
                    },
                    Orientation.Vertical,
                    onDragStarted = {
                        isDragging.value = true
                        draggingStarted = true
                        draggedItem = item
                        draggedItemIndex = index
                        draggedItemOffset = 0f
                    }, onDragStopped = {
                        val newIndex = draggedItemIndex + draggedItemDIndex
                        if (newIndex >= 0 && newIndex < items.value.size) {
                            onMove(item, draggedItemIndex + draggedItemDIndex)
                        }
                        draggedItem = null
                        Thread {
                            draggingStarted = false
                            WaitUtil.sleep(50)
                            if (!draggingStarted) {
                                isDragging.value = false
                            }
                        }.start()
                    })
            ) {
                itemContent(item)
            }
        }
    }
}

@Composable
private fun <T> Modifier.lineOffset(
    draggedItem: T?,
    draggedItemOffset: Float,
    draggedItemIndex: Int,
    draggedItemDIndex: Int,
    item: T,
    index: Int,
    itemHeight: Int
): Modifier {
    return if (draggedItem == null) {
        this.offset { IntOffset(0, 0) }
    } else {
        val dy = getItemOffset(
            draggedItem, draggedItemOffset, draggedItemIndex, draggedItemDIndex, item, index, itemHeight,
        )
        val offset by animateIntOffsetAsState(IntOffset(0, dy.toInt()))
        this.offset { offset }
    }
}

private fun getRealDraggedItemOffset(index: Int, draggedItemOffset: Float, itemsCount: Int, itemHeight: Int): Float {
    val min = -itemHeight * index
    val max = itemHeight * (itemsCount - 1 - index)
    return max(min.toFloat(), min(max.toFloat(), draggedItemOffset))
}

private fun <T> getItemOffset(
    draggedItem: T?,
    draggedItemOffset: Float,
    draggedItemIndex: Int,
    draggedItemDIndex: Int,
    item: T,
    index: Int,
    itemHeight: Int,
): Float {
    if (draggedItem == item) {
        return draggedItemOffset
    } else if (draggedItem != null && draggedItem != item) {
        val dIndex = index - draggedItemIndex
        if (dIndex.sign == draggedItemDIndex.sign && abs(dIndex) <= abs(draggedItemDIndex)) {
            return -itemHeight.toFloat() * dIndex.sign
        }
    }
    return 0f
}
