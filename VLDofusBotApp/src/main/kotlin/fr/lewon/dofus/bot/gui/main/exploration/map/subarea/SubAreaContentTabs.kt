package fr.lewon.dofus.bot.gui.main.exploration.map.subarea

import androidx.compose.runtime.Composable
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.util.UiResource

enum class SubAreaContentTabs(
    val title: String,
    val resource: UiResource,
    val content: @Composable (DofusSubArea) -> Unit
) {
    MONSTERS("Monsters", UiResource.MONSTERS, { SubAreaMonstersContent(it) }),
    RESOURCES("Resources", UiResource.JOBS, { SubAreaResourcesContent(it) }),
    ;
}