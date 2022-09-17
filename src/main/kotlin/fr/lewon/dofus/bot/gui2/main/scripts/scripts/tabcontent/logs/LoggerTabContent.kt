package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs

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
import fr.lewon.dofus.bot.gui2.custom.handPointerIcon
import fr.lewon.dofus.bot.gui2.util.AppColors
import fr.lewon.dofus.bot.model.characters.DofusCharacter

@Composable
fun LoggerTabContent(character: DofusCharacter) {
    val selectedLoggerType = LogsUIState.getLoggerType(character)
    Column {
        TabRow(
            selectedLoggerType.value.ordinal,
            Modifier.height(30.dp),
            backgroundColor = MaterialTheme.colors.background,
            contentColor = AppColors.primaryLightColor,
        ) {
            for (loggerType in LoggerUIType.values()) {
                Tab(
                    text = { Text(loggerType.label) },
                    modifier = Modifier.handPointerIcon(),
                    selected = selectedLoggerType.value == loggerType,
                    unselectedContentColor = Color.LightGray,
                    onClick = { selectedLoggerType.value = loggerType },
                    enabled = true
                )
            }
        }
        LogsContent(selectedLoggerType.value, character)
    }
}