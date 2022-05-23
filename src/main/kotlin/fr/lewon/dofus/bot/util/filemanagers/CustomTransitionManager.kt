package fr.lewon.dofus.bot.util.filemanagers

import fr.lewon.dofus.bot.core.world.WorldGraphUtil

object CustomTransitionManager {

    private val TO_DISABLE_TRANSITION_IDS = listOf(
        519634.0, // 25;-24
        484905.0, // -5;-52
        517173.0, // -1;24
    )

    private val TO_DISABLE_MAP_IDS = listOf(
        1.95301388E8, // -7;3 -> RIGHT
        28049666.0 // -55;14
    )

    private val TO_DISABLE_CRITERION = listOf(
        9.9614726E7 to 9.9614725E7 // 24;-4 to 24;-5
    )

    fun initManager() {
        TO_DISABLE_TRANSITION_IDS.forEach {
            WorldGraphUtil.addInvalidTransitionId(it)
        }
        TO_DISABLE_MAP_IDS.forEach {
            WorldGraphUtil.addInvalidMapId(it)
        }
        TO_DISABLE_CRITERION.forEach {
            val fromMapId = it.first
            val toMapId = it.second
            val vertex = WorldGraphUtil.getVertex(fromMapId, 1)
                ?: error("No vertex found for map : $fromMapId")
            WorldGraphUtil.getOutgoingEdges(vertex)
                .filter { edge -> edge.to.mapId == toMapId }
                .flatMap { edge -> edge.transitions }
                .forEach { transition -> transition.criterion = "" }
        }
    }

}