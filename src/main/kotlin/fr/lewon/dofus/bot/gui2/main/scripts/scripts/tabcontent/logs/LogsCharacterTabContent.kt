package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.SubTitleText
import fr.lewon.dofus.bot.gui2.custom.handPointerIcon
import fr.lewon.dofus.bot.gui2.custom.grayBoxStyle
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
                Box(Modifier.fillMaxSize().padding(5.dp)) {
                    val state = rememberScrollState()
                    Column(
                        Modifier.verticalScroll(state).padding(start = 5.dp, top = 5.dp, bottom = 5.dp, end = 10.dp)
                    ) {

                    }
                    VerticalScrollbar(
                        modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                        adapter = rememberScrollbarAdapter(state),
                    )
                }
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
        selectedCharactersUIStates.indexOfFirst { it.value.name == LogsUIUtil.logsSelectedCharacter.value }
            .takeIf { it >= 0 } ?: 0.also { LogsUIUtil.logsSelectedCharacter.value = "" }
    } else 0
    Column {
        if (ScriptTabsUIUtil.getCurrentTab() == ScriptTab.GLOBAL) {
            TabRow(
                selectedIndex, Modifier.height(30.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions.getOrNull(selectedIndex) ?: tabPositions.first())
                    )
                },
                backgroundColor = MaterialTheme.colors.background,
                contentColor = AppColors.primaryLightColor,
            ) {
                for (characterUIState in selectedCharactersUIStates) {
                    Tab(
                        text = { Text(characterUIState.value.name) },
                        modifier = Modifier.handPointerIcon(),
                        selected = characterUIState.value.name == LogsUIUtil.logsSelectedCharacter.value,
                        unselectedContentColor = Color.LightGray,
                        onClick = { LogsUIUtil.logsSelectedCharacter.value = characterUIState.value.name },
                        enabled = selectedCharactersUIStates.size > 1
                    )
                }
            }
        }
        LoggerTabContent(selectedCharactersUIStates[selectedIndex])
    }
}