package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.handPointerIcon
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTab
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIState
import fr.lewon.dofus.bot.gui2.util.AppColors

@Composable
fun LogsCharacterTabContent() {
    Column {
        Column(
            Modifier.fillMaxHeight()
                .fillMaxWidth()
                .padding(5.dp)
                .border(BorderStroke(1.dp, Color.Gray))
                .background(AppColors.DARK_BG_COLOR)
        ) {
            if (ScriptTabsUIState.getSelectedCharacters().isNotEmpty()) {
                LogsTabsRow()
                Box(Modifier.fillMaxSize().padding(5.dp)) {
                    val state = rememberScrollState()
                    Column(
                        Modifier.verticalScroll(state)
                            .padding(start = 5.dp, top = 5.dp, bottom = 5.dp, end = 10.dp)
                    ) {

                    }
                    VerticalScrollbar(
                        modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                        adapter = rememberScrollbarAdapter(state),
                    )
                }
            }
        }
    }
}

@Composable
private fun LogsTabsRow() {
    val selectedCharacters = ScriptTabsUIState.getSelectedCharacters()
    val selectedIndex = if (ScriptTabsUIState.currentPage.value == ScriptTab.GLOBAL) {
        selectedCharacters.indexOfFirst { it.pseudo == LogsUIState.logsSelectedCharacter.value }
            .takeIf { it >= 0 } ?: 0.also { LogsUIState.logsSelectedCharacter.value = "" }
    } else 0
    Column {
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
            for (character in selectedCharacters) {
                Tab(
                    text = { Text(character.pseudo) },
                    modifier = Modifier.handPointerIcon(),
                    selected = character.pseudo == LogsUIState.logsSelectedCharacter.value,
                    unselectedContentColor = Color.LightGray,
                    onClick = { LogsUIState.logsSelectedCharacter.value = character.pseudo },
                    enabled = selectedCharacters.size > 1
                )
            }
        }
        val character = selectedCharacters[selectedIndex]
        LoggerTabContent(character)
    }
}