package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.gui2.custom.*
import fr.lewon.dofus.bot.gui2.util.AppColors
import fr.lewon.dofus.bot.gui2.util.UiResource

@Composable
fun LogsContent(loggerType: LoggerUIType, characterName: String) {
    val loggerUIState = LogsUIUtil.getLoggerUIState(characterName, loggerType)
    Column(Modifier.fillMaxSize()) {
        LoggerButtonsContent(loggerUIState)
        LogItemsContent(loggerUIState)
    }
}

@Composable
fun LoggerButtonsContent(loggerUIState: MutableState<LoggerUIState>) {
    Row(Modifier.height(30.dp)) {
        val autoScrollEnabled = loggerUIState.value.autoScroll
        AnimatedButton(
            { loggerUIState.value = loggerUIState.value.copy(autoScroll = !autoScrollEnabled) },
            "Auto scroll",
            UiResource.AUTO_SCROLL.imagePainter,
            Modifier.width(100.dp),
            CustomShapes.buildTrapezoidShape(bottomRightDeltaRatio = 0.15f),
            if (autoScrollEnabled) AppColors.primaryLightColor else Color.Gray,
            if (autoScrollEnabled) Color.Black else Color.White
        )
        Spacer(Modifier.weight(1f))
        if (loggerUIState.value.loggerType.canBePaused) {
            val pauseEnabled = loggerUIState.value.pauseLogs
            AnimatedButton(
                { loggerUIState.value = loggerUIState.value.copy(pauseLogs = !pauseEnabled) },
                "Pause",
                UiResource.PAUSE.imagePainter,
                Modifier.width(100.dp),
                CustomShapes.buildTrapezoidShape(bottomRightDeltaRatio = 0.15f, bottomLeftDeltaRatio = 0.15f),
                if (pauseEnabled) AppColors.primaryLightColor else Color.Gray,
                if (pauseEnabled) Color.Black else Color.White
            )
            Spacer(Modifier.weight(1f))
        }
        AnimatedButton(
            {
                loggerUIState.value = loggerUIState.value.copy(
                    logItems = emptyList(),
                    expandedLogItem = null
                )
            },
            "Clear",
            UiResource.ERASE.imagePainter,
            Modifier.width(100.dp),
            CustomShapes.buildTrapezoidShape(bottomLeftDeltaRatio = 0.15f),
            Color.Gray
        )
    }
}

@Composable
fun LogItemsContent(loggerUIState: MutableState<LoggerUIState>) {
    val logItems = loggerUIState.value.logItems.toList()
    Column {
        Box(Modifier.fillMaxSize().weight(1f).padding(5.dp)) {
            val logsScrollState = loggerUIState.value.scrollState
            SelectionContainer {
                Column(Modifier.fillMaxSize().verticalScroll(logsScrollState).padding(end = 14.dp)) {
                    for (logItem in logItems) {
                        if (logItem.description.isEmpty()) {
                            CommonText(logItem.toString())
                        } else {
                            ExpandableLogItemContent(loggerUIState, logItem)
                        }
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(logsScrollState),
            )
        }
        val expandedLogItem = loggerUIState.value.expandedLogItem
        if (expandedLogItem != null && loggerUIState.value.logItems.contains(expandedLogItem)) {
            ExpandedLogItemContent(expandedLogItem)
        }
    }
}

@Composable
private fun ExpandableLogItemContent(loggerUIState: MutableState<LoggerUIState>, logItem: LogItem) {
    val expanded = loggerUIState.value.expandedLogItem == logItem
    Column {
        Row {
            val color = if (expanded) AppColors.primaryColor else Color.White
            Row(Modifier.height(12.dp)) {
                val icon = if (expanded) Icons.Default.Remove else Icons.Default.Add
                ButtonWithTooltip(
                    {
                        loggerUIState.value = loggerUIState.value.copy(
                            expandedLogItem = if (expanded) null else logItem
                        )
                    },
                    title = if (expanded) "Reduce" else "Expand",
                    imageVector = icon,
                    shape = RoundedCornerShape(percent = 5),
                    width = 20.dp,
                    iconColor = color
                )
            }
            Spacer(Modifier.width(5.dp))
            Row(Modifier.align(Alignment.CenterVertically)) {
                CommonText(logItem.toString(), enabledColor = color)
            }
        }
    }
}

@Composable
private fun ExpandedLogItemContent(logItem: LogItem) {
    val descriptionScrollState = remember(logItem) { ScrollState(0) }
    val height = remember { mutableStateOf(10.dp) }
    Box(Modifier.fillMaxWidth().height(min(height.value, 130.dp)).background(AppColors.backgroundColor).padding(5.dp)) {
        Column(
            Modifier.verticalScroll(descriptionScrollState).padding(end = 10.dp)
                .onGloballyPositioned { height.value = it.size.height.dp + 10.dp }
        ) {
            SelectionContainer {
                CommonText(logItem.toString(), fontWeight = FontWeight.Bold, modifier = Modifier.padding(5.dp))
            }
            HorizontalSeparator()
            SelectionContainer {
                CommonText(logItem.description, modifier = Modifier.padding(5.dp))
            }
        }
        VerticalScrollbar(
            modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(descriptionScrollState),
        )
    }
}