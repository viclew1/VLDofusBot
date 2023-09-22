package fr.lewon.dofus.bot.gui.main

import androidx.compose.runtime.Composable
import fr.lewon.dofus.bot.gui.main.auctionhouse.AuctionHouseItemFinderContent
import fr.lewon.dofus.bot.gui.main.characters.CharactersEditionContent
import fr.lewon.dofus.bot.gui.main.devtools.DevToolsContent
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationHelperContent
import fr.lewon.dofus.bot.gui.main.jobs.JobsContent
import fr.lewon.dofus.bot.gui.main.metamob.MetamobHelperContent
import fr.lewon.dofus.bot.gui.main.pathbuilder.PathBuilderContent
import fr.lewon.dofus.bot.gui.main.scripts.ScriptsContent
import fr.lewon.dofus.bot.gui.main.settings.SettingsContent
import fr.lewon.dofus.bot.gui.main.treasure.TreasureHuntHintsContent
import fr.lewon.dofus.bot.gui.util.UiResource

enum class MainAppContent(val title: String, val uiResource: UiResource, val content: @Composable () -> Unit) {
    SCRIPTS("Scripts", UiResource.SCRIPT_LOGO, { ScriptsContent() }),
    CHARACTERS("Characters", UiResource.CHARACTERS_LOGO, { CharactersEditionContent() }),
    METAMOB_HELPER("Metamob Helper", UiResource.METAMOB_HELPER_LOGO, { MetamobHelperContent() }),
    EXPLORATION("Exploration", UiResource.EXPLORATION_HELPER_LOGO, { ExplorationHelperContent() }),
    JOBS("Jobs", UiResource.JOBS, { JobsContent() }),
    PATH_BUILDER("Path Builder", UiResource.PATH, { PathBuilderContent() }),
    AH_ITEM_FINDER("Auction House Item Finder", UiResource.AH_ITEM_FINDER_LOGO, { AuctionHouseItemFinderContent() }),
    TREASURE_HUNT_HINTS("Treasure Hunt Hints", UiResource.TREASURE_HUNT, { TreasureHuntHintsContent() }),
    DEV_TOOLS("Dev Tools", UiResource.DEV_TOOLS_LOGO, { DevToolsContent() }),
    SETTINGS("Settings", UiResource.SETTINGS_LOGO, { SettingsContent() }),
}