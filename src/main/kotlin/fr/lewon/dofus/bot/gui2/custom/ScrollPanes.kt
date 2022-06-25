package fr.lewon.dofus.bot.gui2.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun SimpleVerticalScrollPane(
    modifier: Modifier = Modifier,
    splitPaneState: SplitPaneState = rememberSplitPaneState(),
    minTopSize: Dp = 0.dp,
    minBottomSize: Dp = 0.dp,
    topContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit
) {
    val realMinBottomSize = minBottomSize - minTopSize
    VerticalSplitPane(modifier, splitPaneState) {
        first(minTopSize) {
            topContent()
        }
        second(realMinBottomSize) {
            bottomContent()
        }
        splitter {
            visiblePart {
                Box(Modifier.height(1.dp).fillMaxWidth().background(Color.Gray))
            }
            handle {
                Box(
                    Modifier.markAsHandle().verticalResizePointerIcon().background(Color.LightGray).fillMaxWidth()
                        .height(4.dp)
                )
            }
        }
    }
}