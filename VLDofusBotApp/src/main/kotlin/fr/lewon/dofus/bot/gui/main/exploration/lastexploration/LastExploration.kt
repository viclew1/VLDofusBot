package fr.lewon.dofus.bot.gui.main.exploration.lastexploration

import fr.lewon.dofus.bot.core.model.maps.DofusSubArea

data class LastExploration(
    val progressBySubArea: Map<DofusSubArea, ExplorationProgress> = emptyMap(),
    val explorationStopped: Boolean = false,
) {

    fun getSubAreasToExploreAgain() = progressBySubArea.filter {
        it.value.total == 0 || it.value.current < it.value.total
    }.keys.toList()
}

data class ExplorationProgress(
    val current: Int,
    val total: Int,
    val started: Boolean,
)