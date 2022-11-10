package fr.lewon.dofus.bot.gui2.main.exploration

data class ExplorationUIState(
    val exploredTimeByMap: Map<Double, Long> = HashMap()
)