package fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.selector

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.gui.ComposeUIUtil

object ScriptSelectorUIUtil : ComposeUIUtil() {

    val uiState = mutableStateOf(ScriptSelectorUIState())

}