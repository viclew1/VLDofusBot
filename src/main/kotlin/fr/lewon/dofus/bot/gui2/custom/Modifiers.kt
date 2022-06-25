package fr.lewon.dofus.bot.gui2.custom

import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.*
import java.awt.Cursor

fun Modifier.defaultHoverManager(isHovered: MutableState<Boolean>): Modifier = composed {
    this.pointerInput("") {
        awaitPointerEventScope {
            while (true) {
                val pass = PointerEventPass.Initial
                val event = awaitPointerEvent(pass)
                val isOutsideRelease = event.type == PointerEventType.Release &&
                        event.changes[0].isOutOfBounds(size, Size.Zero)
                isHovered.value = event.type != PointerEventType.Exit && !isOutsideRelease
            }
        }
    }
}

fun Modifier.handPointerIcon(): Modifier = pointerHoverIcon(
    PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
)

fun Modifier.verticalResizePointerIcon(): Modifier = pointerHoverIcon(
    PointerIcon(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR))
)