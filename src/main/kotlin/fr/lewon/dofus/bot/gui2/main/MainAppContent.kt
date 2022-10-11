package fr.lewon.dofus.bot.gui2.main

import androidx.compose.runtime.Composable
import fr.lewon.dofus.bot.gui2.main.exploration.ExplorationHelperContent
import fr.lewon.dofus.bot.gui2.main.metamob.MetamobHelperContent
import fr.lewon.dofus.bot.gui2.main.scripts.ScriptsContent
import fr.lewon.dofus.bot.gui2.main.settings.SettingsContent
import fr.lewon.dofus.bot.gui2.util.UiResource

enum class MainAppContent(val title: String, val uiResource: UiResource, val content: @Composable () -> Unit) {
    SCRIPTS("Scripts", UiResource.SCRIPT_LOGO, { ScriptsContent() }),
    METAMOB("Metamob", UiResource.METAMOB_HELPER_LOGO, { MetamobHelperContent() }),
    EXPLORATION("Exploration", UiResource.EXPLORATION_HELPER_LOGO, { ExplorationHelperContent() }),
    SETTINGS("Settings", UiResource.SETTINGS_LOGO, { SettingsContent() }),
}