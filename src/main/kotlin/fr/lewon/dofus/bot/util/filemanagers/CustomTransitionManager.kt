package fr.lewon.dofus.bot.util.filemanagers

import fr.lewon.dofus.bot.core.world.Transition
import fr.lewon.dofus.bot.core.world.WorldGraphUtil

object CustomTransitionManager {

    private val TO_DISABLE_TRANSITION_IDS = listOf(
        519634.0, // 25;-24
        484905.0, // -5;-52
        517173.0, // -1;24
        435676.0, // -55;15
        473696.0, // 15;21
        523764.0, // -21;37
    )

    private val TO_DISABLE_MAP_IDS = listOf(
        1.95301388E8, // -7;3 -> RIGHT
        28049666.0, // -55;14
        160169984.0, // 33;4 underground
    )

    private val TO_DISABLE_CRITERION = listOf(
        9.9614726E7 to 9.9614725E7 // 24;-4 to 24;-5
    )

    private val TO_DISABLE_TRANSITION_USING_CRITERION = listOf(
        190843395.0 to 190843396.0 // 2;-11 to 2;-10
    )

    fun initManager() {
        TO_DISABLE_TRANSITION_IDS.forEach {
            WorldGraphUtil.addInvalidTransitionId(it)
        }
        TO_DISABLE_MAP_IDS.forEach {
            WorldGraphUtil.addInvalidMapId(it)
        }
        TO_DISABLE_CRITERION.forEach {
            getTransitions(it.first, it.second).forEach { transition -> transition.criterion = "" }
        }
        TO_DISABLE_TRANSITION_USING_CRITERION.forEach {
            getTransitions(it.first, it.second).forEach { transition -> transition.criterion = "(PG=-1)" }
        }
    }

    private fun getTransitions(fromMapId: Double, toMapId: Double): List<Transition> {
        val vertex = WorldGraphUtil.getVertex(fromMapId, 1)
            ?: error("No vertex found for map : $fromMapId")
        return WorldGraphUtil.getOutgoingEdges(vertex)
            .filter { edge -> edge.to.mapId == toMapId }
            .flatMap { edge -> edge.transitions }
    }

}