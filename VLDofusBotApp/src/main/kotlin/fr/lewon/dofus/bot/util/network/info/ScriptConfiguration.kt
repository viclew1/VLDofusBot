package fr.lewon.dofus.bot.util.network.info

class ScriptConfiguration(
    val harvestableSet: Set<Double> = emptySet(),
    val killEverything: Boolean = false,
    val maxMonsterGroupLevel: Int = 0,
    val maxMonsterGroupSize: Int = 0,
)