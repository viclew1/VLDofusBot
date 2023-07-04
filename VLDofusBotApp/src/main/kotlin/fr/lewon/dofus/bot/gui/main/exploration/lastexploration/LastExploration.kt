package fr.lewon.dofus.bot.gui.main.exploration.lastexploration

import fr.lewon.dofus.bot.core.model.maps.DofusSubArea

data class LastExploration(
    val subAreasToExplore: List<DofusSubArea>,
    val currentSubAreaId: Double = -1.0,
    val currentAreaTotalCount: Int = -1,
    val currentAreaProgress: Int = -1,
    val explorationStopped: Boolean = false,
    val explorationFinished: Boolean = false
)