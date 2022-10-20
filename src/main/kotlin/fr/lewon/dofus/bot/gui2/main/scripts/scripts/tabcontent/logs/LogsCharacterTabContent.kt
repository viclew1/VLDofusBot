package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.SubTitleText
import fr.lewon.dofus.bot.gui2.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui2.custom.handPointerIcon
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTab
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIUtil
import fr.lewon.dofus.bot.gui2.util.AppColors

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
    val selectedIndex = if (ScriptTabsUIUtil.getCurrentTab() == ScriptTab.GLOBAL) {
        selectedCharactersUIStates.indexOfFirst {
            it.value.name == LogsUIUtil.currentLoggerUIState.value.characterName
        }.takeIf { it >= 0 } ?: 0.also {
            LogsUIUtil.currentLoggerUIState.value = LogsUIUtil.currentLoggerUIState.value.copy(
                characterName = ""
            )
        }
    } else 0
    Column {
        if (ScriptTabsUIUtil.getCurrentTab() == ScriptTab.GLOBAL) {
            TabRow(
                selectedIndex, Modifier.height(30.dp),
                backgroundColor = MaterialTheme.colors.background,
                contentColor = AppColors.primaryLightColor,
            ) {
                for (characterUIState in selectedCharactersUIStates) {
                    Tab(
                        text = { Text(characterUIState.value.name) },
                        modifier = Modifier.handPointerIcon(),
                        selected = characterUIState.value.name == LogsUIUtil.currentLoggerUIState.value.characterName,
                        unselectedContentColor = Color.LightGray,
                        onClick = {
                            LogsUIUtil.currentLoggerUIState.value = LogsUIUtil.currentLoggerUIState.value.copy(
                                characterName = characterUIState.value.name
                            )
                        },
                        enabled = selectedCharactersUIStates.size > 1
                    )
                }
            }
        }
        LoggerTabContent(selectedCharactersUIStates[selectedIndex])
    }
}