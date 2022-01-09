package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.manager.world.Edge
import fr.lewon.dofus.bot.core.manager.world.Transition
import fr.lewon.dofus.bot.core.manager.world.TransitionType
import fr.lewon.dofus.bot.core.manager.world.WorldGraphUtil
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MoveTask
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.CurrentMapMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.SetCharacterRestrictionsMessage
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

object MoveUtil {

    fun buildDirectionalMoveTask(gameInfo: GameInfo, direction: Direction, amount: Int = 1): MoveTask {
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Can't find player cell id")
        val cellData = gameInfo.completeCellDataByCellId[playerCellId]?.cellData
            ?: error("Can't find player cell data")

        val path = ArrayList<Transition>()
        var currentVertex = WorldGraphUtil.getVertex(gameInfo.currentMap.id, cellData.getLinkedZoneRP())
            ?: error("No vertex found")
        for (i in 0 until amount) {
            val edges = WorldGraphUtil.getOutgoingEdges(currentVertex)
            val transitionsByEdge = edges.associateWith { it.transitions }
            val edgeWithTransition = chooseEdgeWithTransition(gameInfo, transitionsByEdge, direction)
            currentVertex = edgeWithTransition.first.to
            path.add(edgeWithTransition.second)
        }
        return MoveTask(path)
    }

    private fun chooseEdgeWithTransition(
        gameInfo: GameInfo,
        transitionsByEdge: Map<Edge, List<Transition>>,
        direction: Direction
    ): Pair<Edge, Transition> {
        for (edgeWithTransitions in transitionsByEdge.entries) {
            val edge = edgeWithTransitions.key
            val transitions = edgeWithTransitions.value
            val validTransition = transitions.firstOrNull { it.direction == direction.directionInt }
            if (validTransition != null) {
                return edge to validTransition
            }
        }
        val edgeWithTransitionList = transitionsByEdge.entries.flatMap { e ->
            val edge = e.key
            val transitions = e.value
            transitions
                .filter { it.type == TransitionType.INTERACTIVE || it.type == TransitionType.MAP_ACTION }
                .map { edge to it }
        }
        return when (direction) {
            Direction.LEFT -> edgeWithTransitionList.minByOrNull { getTransitionLocation(gameInfo, it.second).x }
            Direction.RIGHT -> edgeWithTransitionList.maxByOrNull { getTransitionLocation(gameInfo, it.second).x }
            Direction.TOP -> edgeWithTransitionList.minByOrNull { getTransitionLocation(gameInfo, it.second).y }
            Direction.BOTTOM -> edgeWithTransitionList.maxByOrNull { getTransitionLocation(gameInfo, it.second).y }
        } ?: error("No path found")
    }

    private fun getTransitionLocation(gameInfo: GameInfo, transition: Transition): PointRelative {
        return when (transition.type) {
            TransitionType.MAP_ACTION -> gameInfo.dofusBoard.getCell(transition.cellId).bounds.getCenter()
            TransitionType.INTERACTIVE -> InteractiveUtil.getElementClickPosition(gameInfo, transition.id.toInt())
            else -> error("Transition not supported : ${transition.type}")
        }
    }

    fun processMove(gameInfo: GameInfo, clickLocation: PointRelative): Boolean {
        gameInfo.eventStore.clear()
        MouseUtil.leftClick(gameInfo, clickLocation)
        WaitUtil.waitUntilMultipleMessagesArrive(
            gameInfo,
            CurrentMapMessage::class.java,
            MapComplementaryInformationsDataMessage::class.java,
            SetCharacterRestrictionsMessage::class.java
        )
        waitForMapChange(gameInfo)
        return true
    }

    fun isMapChanged(
        gameInfo: GameInfo,
        complementaryInformationClass: Class<out MapComplementaryInformationsDataMessage> = MapComplementaryInformationsDataMessage::class.java
    ): Boolean {
        return gameInfo.eventStore.getAllEvents(SetCharacterRestrictionsMessage::class.java).isNotEmpty()
                && gameInfo.eventStore.getAllEvents(CurrentMapMessage::class.java).isNotEmpty()
                && gameInfo.eventStore.getAllEvents(complementaryInformationClass).isNotEmpty()
    }

    fun waitForMapChange(
        gameInfo: GameInfo,
        complementaryInformationClass: Class<out MapComplementaryInformationsDataMessage> = MapComplementaryInformationsDataMessage::class.java
    ) {
        WaitUtil.waitForEvents(
            gameInfo,
            complementaryInformationClass,
            CurrentMapMessage::class.java,
            SetCharacterRestrictionsMessage::class.java,
        )
        gameInfo.eventStore.clearUntilLast(CurrentMapMessage::class.java)
        WaitUtil.waitForEvents(gameInfo, SetCharacterRestrictionsMessage::class.java)
        gameInfo.eventStore.clearUntilFirst(SetCharacterRestrictionsMessage::class.java)
        WaitUtil.waitForEvent(gameInfo, BasicNoOperationMessage::class.java)
    }

}