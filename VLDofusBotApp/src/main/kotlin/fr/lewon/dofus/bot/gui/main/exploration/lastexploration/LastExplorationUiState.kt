package fr.lewon.dofus.bot.gui.main.exploration.lastexploration

data class LastExplorationUiState(
    val lastExplorationByCharacter: Map<String, LastExploration<*>> = emptyMap(),
)