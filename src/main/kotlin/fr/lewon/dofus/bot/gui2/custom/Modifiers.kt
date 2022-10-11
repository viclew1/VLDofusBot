package fr.lewon.dofus.bot.gui2.custom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.util.AppColors
import java.awt.Cursor

fun Modifier.defaultHoverManager(
    isHovered: MutableState<Boolean>,
    onHover: () -> Unit = {},
    onExit: () -> Unit = {}
): Modifier = composed {
    this.pointerInput("") {
        awaitPointerEventScope {
            while (true) {
                val pass = PointerEventPass.Initial
                val event = awaitPointerEvent(pass)
                val isOutsideRelease = event.type == PointerEventType.Release &&
                        event.changes[0].isOutOfBounds(size, Size.Zero)
                val newIsHovered = event.type != PointerEventType.Exit && !isOutsideRelease
                if (isHovered.value != newIsHovered) {
                    isHovered.value = newIsHovered
                    if (newIsHovered) {
                        onHover()
                    } else {
                        onExit()
                    }
                }
            }
        }
    }
}

fun Modifier.handPointerIcon(): Modifier = pointerHoverIcon(
    PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
)

fun Modifier.grayBoxStyle(): Modifier {
    return background(AppColors.DARK_BG_COLOR).border(BorderStroke(1.dp, Color.Gray))
}