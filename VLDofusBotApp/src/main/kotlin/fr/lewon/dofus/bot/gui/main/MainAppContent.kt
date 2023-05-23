package fr.lewon.dofus.bot.gui.main

import androidx.compose.runtime.Composable
import fr.lewon.dofus.bot.gui.main.auctionhouse.AuctionHouseItemFinderContent
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationHelperContent
import fr.lewon.dofus.bot.gui.main.metamob.MetamobHelperContent
import fr.lewon.dofus.bot.gui.main.scripts.ScriptsContent
import fr.lewon.dofus.bot.gui.main.settings.SettingsContent
import fr.lewon.dofus.bot.gui.util.UiResource

enum class MainAppContent(val title: String, val uiResource: UiResource, val content: @Composable () -> Unit) {
    SCRIPTS("Scripts", UiResource.SCRIPT_LOGO, { ScriptsContent() }),
    METAMOB("Metamob", UiResource.METAMOB_HELPER_LOGO, { MetamobHelperContent() }),
    EXPLORATION("Exploration", UiResource.EXPLORATION_HELPER_LOGO, { ExplorationHelperContent() }),
    AH_ITEM_FINDER("Auction House Item Finder", UiResource.AH_ITEM_FINDER_LOGO, { AuctionHouseItemFinderContent() }),
    SETTINGS("Settings", UiResource.SETTINGS_LOGO, { SettingsContent() }),
}