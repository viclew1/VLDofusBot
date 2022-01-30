package fr.lewon.dofus.bot.model.dungeon

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap

object Dungeons {
    val BOUFTOU_DUNGEON = Dungeon(getMap(120063489.0), 173, listOf(0, 0), 173, listOf(0))
    val CHAMPS_DUNGEON = Dungeon(getMap(1.92937992E8), 780, listOf(0, 0), 780, listOf(0))
    val ENSABLE_DUNGEON = Dungeon(getMap(1.90056961E8), 798, listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), 798, listOf(0, 0))
    val DRAEGNERYS_DUNGEON = Dungeon(getMap(8.4411397E7), 4946, listOf(0, 0), 4947, listOf(0))
    val BRAKMAR_RATS_DUNGEON = Dungeon(getMap(2.16927751E8), 800, listOf(0, 0), 6614, listOf(0))


    private fun getMap(mapId: Double): DofusMap {
        return MapManager.getDofusMap(mapId)
    }
}