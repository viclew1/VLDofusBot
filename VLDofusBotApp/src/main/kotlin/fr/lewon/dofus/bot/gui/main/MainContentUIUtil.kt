package fr.lewon.dofus.bot.gui.main

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.gui.ComposeUIUtil

object MainContentUIUtil : ComposeUIUtil() {

    val mainContentUIState = mutableStateOf(MainContentUIState())

}