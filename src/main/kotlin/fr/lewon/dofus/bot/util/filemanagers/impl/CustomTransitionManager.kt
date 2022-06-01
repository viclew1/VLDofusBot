package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.core.world.Transition
import fr.lewon.dofus.bot.core.world.TransitionType
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager

object CustomTransitionManager : ToInitManager {

    private const val IMPOSSIBLE_CRITERION = "(PG=-1)"

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

    private val TO_UPDATE_TRANSITION = listOf(
        TransitionUpdate(
            28049920.0,
            24118282.0,
            { it.type == TransitionType.SCROLL_ACTION },
            { it.criterion = IMPOSSIBLE_CRITERION }), // -53;16 to -54;15
        TransitionUpdate(
            190843396.0,
            190843395.0,
            { it.type == TransitionType.MAP_ACTION },
            { it.cellId = 24 }), // 2;-10 to 2;-11
        TransitionUpdate(
            190843396.0,
            190843395.0,
            { it.type == TransitionType.SCROLL_ACTION && it.cellId == 167 },
            { it.criterion = IMPOSSIBLE_CRITERION }), // 2;-10 to 3;-10
        TransitionUpdate(9.9614726E7, 9.9614725E7, { true }, { it.criterion = "" }), // 24;-4 to 24;-5
    )

    override fun initManager() {
        TO_DISABLE_TRANSITION_IDS.forEach {
            WorldGraphUtil.addInvalidTransitionId(it)
        }
        TO_DISABLE_MAP_IDS.forEach {
            WorldGraphUtil.addInvalidMapId(it)
        }
        TO_UPDATE_TRANSITION.forEach { transitionUpdate ->
            getTransitions(transitionUpdate.fromMapId, transitionUpdate.toMapId)
                .filter { transitionUpdate.transitionCondition(it) }
                .forEach { transitionUpdate.update(it) }
        }
    }

    private fun getTransitions(fromMapId: Double, toMapId: Double): List<Transition> {
        val vertex = WorldGraphUtil.getVertex(fromMapId, 1)
            ?: error("No vertex found for map : $fromMapId")
        return WorldGraphUtil.getOutgoingEdges(vertex)
            .filter { edge -> edge.to.mapId == toMapId }
            .flatMap { edge -> edge.transitions }
    }

    private class TransitionUpdate(
        val fromMapId: Double,
        val toMapId: Double,
        val transitionCondition: (Transition) -> Boolean,
        val update: (Transition) -> Unit
    )

}