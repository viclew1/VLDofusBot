package fr.lewon.dofus.bot.gui.main.exploration.lastexploration

import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues

abstract class LastExploration<T>(
    val progressByItem: Map<T, ExplorationProgress> = emptyMap(),
) {

    var explorationStopped: Boolean = false

    abstract fun copy(
        progressByItem: Map<T, ExplorationProgress> = this.progressByItem.toMap(),
        explorationStopped: Boolean = this.explorationStopped
    ): LastExploration<T>

    abstract fun getLabel(item: T): String

    abstract fun updateParameterValues(parameterValues: ParameterValues)

    fun getItemsToExploreAgain() = progressByItem.filter {
        it.value.total == 0 || it.value.current < it.value.total
    }.keys.toList()

}

data class ExplorationProgress(
    val current: Int,
    val total: Int,
    val started: Boolean,
)