package fr.lewon.dofus.bot.gui2.main.scripts.status

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.AnimatedButton
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.custom.CustomShapes
import fr.lewon.dofus.bot.gui2.util.AppColors

@Composable
fun StatusBarContent() {
    val showHistory = remember { mutableStateOf(false) }
    val statusBarUIState = StatusBarUIUtil.statusBarUIState.value
    Row(
        Modifier.border(BorderStroke(1.dp, AppColors.DARK_BG_COLOR)).fillMaxWidth()
            .background(AppColors.backgroundColor)
    ) {
        Column(Modifier.align(Alignment.CenterVertically)) {
            val history = statusBarUIState.oldMessages
            AnimatedVisibility(showHistory.value, enter = expandVertically(), exit = shrinkVertically()) {
                Column {
                    for (entry in history) {
                        HistoryLine(entry)
                    }
                }
            }
            Row {
                HistoryLine(statusBarUIState.currentStatus)
                Spacer(Modifier.weight(1f))

                Row(Modifier.height(30.dp)) {
                    if (history.isNotEmpty()) {
                        AnimatedButton(
                            { showHistory.value = !showHistory.value },
                            if (showHistory.value) "Reduce" else "Expand",
                            if (showHistory.value) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            Modifier.width(100.dp).fillMaxHeight(),
                            CustomShapes.buildTrapezoidShape(topLeftDeltaRatio = 0.15f),
                            if (showHistory.value) AppColors.primaryLightColor else Color.Gray,
                            if (showHistory.value) Color.Black else Color.White,
                            if (showHistory.value) Color.Black else Color.White,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryLine(text: String) {
    Row(Modifier.padding(5.dp)) {
        SelectionContainer {
            CommonText(text)
        }
    }
}