package fr.lewon.dofus.bot.gui2.custom

import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun rememberScrollbarAdapter(
    lazyGridState: LazyGridState
): ScrollbarAdapter = remember(lazyGridState) {
    ScrollbarAdapter(lazyGridState)
}

fun ScrollbarAdapter(
    lazyGridState: LazyGridState
): ScrollbarAdapter = ScrollableScrollbarAdapter(lazyGridState)

private class ScrollableScrollbarAdapter(
    private val scrollState: LazyGridState
) : ScrollbarAdapter {
    override val scrollOffset: Float
        get() = scrollState.firstVisibleItemIndex / 4 * averageItemSize + scrollState.firstVisibleItemScrollOffset

    override suspend fun scrollTo(containerSize: Int, scrollOffset: Float) {
        scrollState.scrollBy(scrollOffset - this.scrollOffset)
    }

    override fun maxScrollOffset(containerSize: Int) =
        (averageItemSize * itemCount / 4 - containerSize).coerceAtLeast(0f)

    private val itemCount get() = scrollState.layoutInfo.totalItemsCount

    private val averageItemSize by derivedStateOf {
        scrollState
            .layoutInfo
            .visibleItemsInfo
            .asSequence()
            .map { it.size.height }
            .average()
            .toFloat()
    }
}