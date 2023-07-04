package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.core.criterion.DofusCriterionParser
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.core.world.Edge
import fr.lewon.dofus.bot.core.world.Transition
import fr.lewon.dofus.bot.core.world.TransitionType
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import fr.lewon.dofus.bot.model.criterion.DofusCustomCriterion
import fr.lewon.dofus.bot.model.criterion.impl.IsOtomaiTransporterAvailableCriterion
import fr.lewon.dofus.bot.model.transition.CustomTransition
import fr.lewon.dofus.bot.model.transition.NpcTransition
import fr.lewon.dofus.bot.model.transition.zaap.ZaapCriterionKey
import fr.lewon.dofus.bot.model.transition.zaap.ZaapTransition
import fr.lewon.dofus.bot.model.transition.zaap.ZaapTransitionCriterion
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import fr.lewon.dofus.bot.util.game.TravelUtil
import org.reflections.Reflections

object CustomTransitionManager : ToInitManager {

    private const val IMPOSSIBLE_CRITERION = "(PG=-1)"

    private val TO_DISABLE_INTERACTIVE_IDS = listOf(
        519634.0, // 25;-24
        484905.0, // -5;-52
        517173.0, // -1;24
        435676.0, // -55;15
        473696.0, // 15;21
        523764.0, // -21;37
        472865.0, // 22;19
        473019.0, // 11;10
        516693.0, // -5;-9
        516586.0, // -25;-52
        521864.0, // -36;-54
    )

    private val TO_DISABLE_MAP_IDS = listOf(
        1.95301388E8, // -7;3 -> RIGHT
        28049666.0, // -55;14
        160169984.0, // 33;4 underground
        146187.0 // unknown
    )

    private val TO_UPDATE_TRANSITIONS = listOf(
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
        TransitionUpdate(
            70778880.0,
            185863682.0,
            { it.type == TransitionType.SCROLL_ACTION && it.cellId == 533 },
            { it.criterion = IMPOSSIBLE_CRITERION }), // 1;-8 to 1;-7
    )

    private val TO_CREATE_TRANSITIONS = listOf(
        TransitionCreate(
            fromMapId = 100141313.0,
            toMapId = 100141827.0,
            type = TransitionType.SCROLL,
            direction = Direction.TOP.directionInt,
            cellId = 8
        )
    )

    override fun initManager() {
        TO_DISABLE_INTERACTIVE_IDS.forEach {
            WorldGraphUtil.addInvalidInteractiveId(it)
        }
        TO_DISABLE_MAP_IDS.forEach {
            WorldGraphUtil.addInvalidMapId(it)
        }
        TO_UPDATE_TRANSITIONS.forEach { transitionUpdate ->
            getTransitions(transitionUpdate.fromMapId, transitionUpdate.toMapId)
                .filter { transitionUpdate.updateIf(it) }
                .forEach { transitionUpdate.update(it) }
        }
        TO_CREATE_TRANSITIONS.forEach { transitionCreate ->
            val edge = getEdge(transitionCreate.fromMapId, transitionCreate.toMapId)
            edge.transitions.add(
                Transition(
                    edge,
                    direction = transitionCreate.direction,
                    type = transitionCreate.type,
                    skillId = transitionCreate.skillId,
                    criterion = transitionCreate.criterion,
                    transitionMapId = transitionCreate.toMapId,
                    cellId = transitionCreate.cellId,
                    id = transitionCreate.id
                )
            )
        }
        registerCustomCriteria()
        val allNewTransitions = ArrayList<CustomTransition>()
        allNewTransitions.addAll(getNpcTransitions())
        allNewTransitions.addAll(getZaapTransitions())
        allNewTransitions.forEach { transition ->
            if (!transition.edge.transitions.contains(transition)) {
                transition.edge.transitions.add(transition)
            }
        }
        DofusCriterionParser.registerCustomCriterion(ZaapCriterionKey) { _, expectedValue ->
            ZaapTransitionCriterion(expectedValue.toDouble())
        }
    }

    private fun getZaapTransitions(): List<CustomTransition> {
        val zaapVertices = TravelUtil.getAllZaapMaps()
            .mapNotNull { WorldGraphUtil.getVertex(it.id, 1) }
        val verticesWhereZaapsAccessible = MapManager.getAllMaps()
            .filter { it.canReachHavenBag || it.subArea.id == 851.0 } // Haven bags sub area
            .flatMap { WorldGraphUtil.getVertices(it.id) }
        return verticesWhereZaapsAccessible.flatMap { fromVertex ->
            zaapVertices.map { zaapVertex ->
                ZaapTransition(WorldGraphUtil.addEdge(fromVertex, zaapVertex))
            }
        }
    }

    private fun getNpcTransitions() = listOf(
        NpcTransition(getEdge(205261569.0, 206831621.0), 4102, listOf(34493)), // 27;-27 -> 34;-40
        NpcTransition(getEdge(206831621.0, 205261569.0), 4102, listOf(34495)), // 34:-40 -> 27;-27
        NpcTransition(getEdge(159744.0, 160513.0), 928, listOf(3616)), // -56;0 -> -57;-1
        NpcTransition(getEdge(161029.0, 162055.0), 927, listOf(3632)), // -58;-5 -> -60;-7
        NpcTransition(getEdge(167381514.0, 153360.0), 770, listOf(2776, 2777, 2865)), // -36;-10 -> -43;-16
        NpcTransition(getEdge(153360.0, 167381514.0), 770, listOf(2866, 35631)), // -43;-16 -> -36;-10
        NpcTransition(getEdge(54176040.0, 140510209.0), 2632, listOf(22670, 22669)), // -84;-40 -> -83;-58
        NpcTransition(getEdge(140510209.0, 54176040.0), 2632, listOf(22740)), // -83;-58 -> -84;-40
        NpcTransition(getEdge(199230724.0, 198181888.0), 789, listOf(2876)), // -4;24 -> Drake sanctuary
        NpcTransition(getEdge(181928961.0, 181929473.0), 983, listOf(4012)), // -22;13 -> -4;12
        NpcTransition(getEdge(181929473.0, 181928961.0), 983, listOf(4012)), // -4;12 -> -22;13
        NpcTransition(
            getEdge(164102659.0, 179307526.0), 3732, listOf(32280, 32277, 32288, 32293, 32292)
        ), // -28;-31 -> -65;34
        NpcTransition(getEdge(179307526.0, 164102659.0), 3732, listOf(32276)), // -65;34 -> -28;-31
        NpcTransition(getEdge(8.8086292E7, 1.76556034E8), 3717, listOf(32051)), // 12;3 -> 7;-66
        NpcTransition(getEdge(1.76556034E8, 8.8086292E7), 3717, listOf(32052)), // 7;-66 -> 12;3
        NpcTransition(getEdge(8.8213267E7, 2.23482635E8), 6885, listOf(69540, 69541)), // -2;4 -> 32;-83
        NpcTransition(getEdge(2.23482635E8, 8.8213267E7), 6885, listOf(69554, 69551)), // 32;-83 -> -2;4
        * getOtomaiTransportersTransitions().toTypedArray(),
        *getFrigostTransportersTransitions().toTypedArray(),
    )

    private fun registerCustomCriteria() {
        Reflections(DofusCustomCriterion::class.java.packageName)
            .getSubTypesOf(DofusCustomCriterion::class.java)
            .mapNotNull { it.kotlin.objectInstance }
            .forEach { DofusCriterionParser.registerCustomCriterion(it.generateKey()) { _, _ -> it } }
    }

    private fun getOtomaiTransportersTransitions(): List<CustomTransition> {
        val criterionStr = "${IsOtomaiTransporterAvailableCriterion.generateKey()}=1"
        return listOf(
            NpcTransition(getEdge(20973058.0, 159766.0), 935, listOf(64326), criterionStr), // -54;19 -> -56;22
            NpcTransition(getEdge(20973058.0, 156174.0), 935, listOf(64327), criterionStr), // -54;19 -> -49;14
            NpcTransition(getEdge(20973058.0, 160260.0), 935, listOf(64328), criterionStr), // -54;19 -> -57;4
        )
    }

    private fun getFrigostTransportersTransitions(): List<CustomTransition> {
        return listOf(
            NpcTransition(getEdge(60035079.0, 54167842.0), 1286, listOf(8133)), // -76;-66 -> -68;-34
            NpcTransition(getEdge(60035079.0, 54161738.0), 1286, listOf(8135)), // -76;-66 -> -56;-74
            NpcTransition(getEdge(60035079.0, 54168407.0), 1286, listOf(8136)), // -76;-66 -> -69;-87
            NpcTransition(getEdge(60035079.0, 54173010.0), 1286, listOf(8137)), // -76;-66 -> -78;-82
            NpcTransition(getEdge(60035079.0, 54166849.0), 1286, listOf(8473)), // -76;-66 -> -66;-65
            NpcTransition(getEdge(60035079.0, 54165320.0), 1286, listOf(8532)), // -76;-66 -> -63;-72
            NpcTransition(getEdge(60035079.0, 54161193.0), 1286, listOf(17862)), // -76;-66 -> -55;-41
        )
    }

    private fun getEdge(fromMapId: Double, toMapId: Double): Edge {
        val fromVertex = WorldGraphUtil.getVertex(fromMapId, 1)
            ?: error("Couldn't find vertex for map : $fromMapId")
        val toVertex = WorldGraphUtil.getVertex(toMapId, 1)
            ?: error("Couldn't find vertex for map : $toMapId")
        return WorldGraphUtil.addEdge(fromVertex, toVertex)
    }

    override fun getNeededManagers(): List<ToInitManager> {
        return emptyList()
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
        val updateIf: (Transition) -> Boolean,
        val update: (Transition) -> Unit
    )

    private class TransitionCreate(
        val fromMapId: Double,
        val toMapId: Double,
        var type: TransitionType,
        var direction: Int = 0,
        var skillId: Int = 0,
        var criterion: String = "",
        var cellId: Int = 0,
        var id: Double = 0.0
    )

}