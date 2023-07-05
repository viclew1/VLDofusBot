package fr.lewon.dofus.bot.gui.main.exploration.lastexploration

import fr.lewon.dofus.bot.core.model.maps.DofusSubArea

data class LastExploration(
    val progressBySubArea: Map<DofusSubArea, ExplorationProgress> = emptyMap(),
    val explorationStopped: Boolean = false,
)

data class ExplorationProgress(
    val current: Int,
    val total: Int,
    val started: Boolean,
)