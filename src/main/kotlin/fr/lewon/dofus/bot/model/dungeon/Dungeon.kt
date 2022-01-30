package fr.lewon.dofus.bot.model.dungeon

import fr.lewon.dofus.bot.core.model.maps.DofusMap

data class Dungeon(
    val map: DofusMap,
    val npcEnterId: Int,
    val npcEnterOptions: List<Int>,
    val npcExitId: Int,
    val npcExitOptions: List<Int>
)