package fr.lewon.dofus.bot.gui.main.scripts.logs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.handPointerIcon
import fr.lewon.dofus.bot.gui.main.characters.CharacterUIState
import fr.lewon.dofus.bot.gui.util.AppColors

@Composable
fun LoggerTabContent(characterUIState: CharacterUIState) {
    val selectedLoggerType = LogsUIUtil.currentLoggerUIState.value.loggerType
    Column {
        TabRow(
            selectedLoggerType.ordinal,
            Modifier.height(30.dp),
            backgroundColor = MaterialTheme.colors.background,
            contentColor = AppColors.primaryLightColor,
        ) {
            for (loggerType in LoggerUIType.entries) {
                Tab(
                    text = { Text(loggerType.label) },
                    modifier = Modifier.handPointerIcon(),
                    selected = selectedLoggerType == loggerType,
                    unselectedContentColor = Color.LightGray,
                    onClick = {
                        LogsUIUtil.currentLoggerUIState.value = LogsUIUtil.currentLoggerUIState.value.copy(
                            loggerType = loggerType
                        )
                    },
                    enabled = true
                )
            }
        }
        LogsContent(selectedLoggerType, characterUIState.name)
    }
}