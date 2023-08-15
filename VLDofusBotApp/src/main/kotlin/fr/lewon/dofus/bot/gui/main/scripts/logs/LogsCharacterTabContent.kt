package fr.lewon.dofus.bot.gui.main.scripts.logs

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.SubTitleText
import fr.lewon.dofus.bot.gui.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui.custom.handPointerIcon
import fr.lewon.dofus.bot.gui.main.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors

@Composable
fun LogsCharacterTabContent() {
    Column {
        Column(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle()) {
            if (CharactersUIUtil.getSelectedCharactersUIStates().isNotEmpty()) {
                LogsTabsRow()
            } else {
                SubTitleText(
                    "Select at least one character to display logs.",
                    Modifier.fillMaxWidth().height(50.dp).padding(10.dp),
                )
            }
        }
    }
}

@Composable
private fun LogsTabsRow() {
    val selectedCharactersUIStates = CharactersUIUtil.getSelectedCharactersUIStates()
    val selectedIndex = selectedCharactersUIStates.indexOfFirst {
        it.name == LogsUIUtil.currentLoggerUIState.value.characterName
    }.takeIf { it >= 0 } ?: 0.also {
        LogsUIUtil.currentLoggerUIState.value = LogsUIUtil.currentLoggerUIState.value.copy(characterName = "")
    }
    Column {
        TabRow(
            selectedIndex, Modifier.height(30.dp),
            backgroundColor = MaterialTheme.colors.background,
            contentColor = AppColors.primaryLightColor,
        ) {
            for (characterUIState in selectedCharactersUIStates) {
                Tab(
                    text = { Text(characterUIState.name) },
                    modifier = Modifier.handPointerIcon(),
                    selected = characterUIState.name == LogsUIUtil.currentLoggerUIState.value.characterName,
                    unselectedContentColor = Color.LightGray,
                    onClick = {
                        LogsUIUtil.currentLoggerUIState.value = LogsUIUtil.currentLoggerUIState.value.copy(
                            characterName = characterUIState.name
                        )
                    },
                    enabled = selectedCharactersUIStates.size > 1
                )
            }
        }
        val displayedCharacter = selectedCharactersUIStates.getOrNull(selectedIndex)
        if (displayedCharacter != null) {
            LoggerTabContent(displayedCharacter)
        }
    }
}