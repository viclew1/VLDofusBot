package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusCoordinates
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.core.world.Transition
import fr.lewon.dofus.bot.core.world.TransitionType
import fr.lewon.dofus.bot.core.world.Vertex
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.CurrentMapMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.SetCharacterRestrictionsMessage
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

object MoveUtil {

    fun buildDirectionalPath(gameInfo: GameInfo, direction: Direction, amount: Int): List<Transition>? {
        return buildDirectionalPath(gameInfo, direction, { _, count -> count == amount }, amount)
    }

    fun buildDirectionalPath(
        gameInfo: GameInfo, direction: Direction, stopFunc: (DofusMap, Int) -> Boolean, limit: Int
    ): List<Transition>? {
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Can't find player cell id")
        val cellData = gameInfo.completeCellDataByCellId[playerCellId]?.cellData
            ?: error("Can't find player cell data")
        val startVertex = WorldGraphUtil.getVertex(gameInfo.currentMap.id, cellData.getLinkedZoneRP())
            ?: error("No vertex found")
        return buildDirectionalPath(startVertex, direction, stopFunc, limit)
    }

    fun buildDirectionalPath(
        startVertex: Vertex, direction: Direction, stopFunc: (DofusMap, Int) -> Boolean, limit: Int
    ): List<Transition>? {
        val path = ArrayList<Transition>()
        var currentVertex = startVertex
        while (path.size < limit) {
            val transition = getDirectionalTransition(currentVertex, direction)
            currentVertex = transition.edge.to
            path.add(transition)
            if (stopFunc(MapManager.getDofusMap(currentVertex.mapId), path.size)) {
                return path
            }
        }
        return null
    }

    private fun getDirectionalTransition(fromVertex: Vertex, direction: Direction): Transition {
        val fromMap = MapManager.getDofusMap(fromVertex.mapId)
        val edges = WorldGraphUtil.getOutgoingEdges(fromVertex)
        val transitions = edges.flatMap { it.transitions }
        for (transition in transitions) {
            val validTransition = transitions.firstOrNull { it.direction == direction.directionInt }
            if (validTransition != null) {
                return validTransition
            }
        }
        val specialTransitions = transitions
            .filter { it.type == TransitionType.INTERACTIVE || it.type == TransitionType.MAP_ACTION }

        val potentialTransitions = getPotentialTransitions(direction, specialTransitions, fromMap)
        val idealDestination = getIdealDestination(direction, fromMap)
        return getClosestTransition(potentialTransitions, idealDestination.x, idealDestination.y)
            ?: error("No path found")
    }

    private fun getIdealDestination(direction: Direction, fromMap: DofusMap): DofusCoordinates {
        return when (direction) {
            Direction.LEFT -> DofusCoordinates(fromMap.posX - 1, fromMap.posY)
            Direction.RIGHT -> DofusCoordinates(fromMap.posX + 1, fromMap.posY)
            Direction.TOP -> DofusCoordinates(fromMap.posX, fromMap.posY - 1)
            Direction.BOTTOM -> DofusCoordinates(fromMap.posX, fromMap.posY + 1)
        }
    }

    private fun getPotentialTransitions(
        direction: Direction,
        transitions: List<Transition>,
        fromMap: DofusMap
    ): List<Transition> {
        val fromMapCoordinates = fromMap.getCoordinates()
        return transitions.filter {
            val destMapCoordinates = MapManager.getDofusMap(it.edge.to.mapId).getCoordinates()
            when (direction) {
                Direction.LEFT -> destMapCoordinates.x < fromMapCoordinates.x
                Direction.RIGHT -> destMapCoordinates.x > fromMapCoordinates.x
                Direction.TOP -> destMapCoordinates.y < fromMapCoordinates.y
                Direction.BOTTOM -> destMapCoordinates.y > fromMapCoordinates.y
            }
        }
    }

    private fun getClosestTransition(transitions: List<Transition>, x: Int, y: Int): Transition? {
        val targetCoordinates = DofusCoordinates(x, y)
        return transitions.minByOrNull {
            MapManager.getDofusMap(it.edge.to.mapId).getCoordinates().distanceTo(targetCoordinates)
        }
    }

    fun processCellMove(gameInfo: GameInfo, cellId: Int): Boolean {
        return processMove(gameInfo, InteractiveUtil.getCellClickPosition(gameInfo, cellId))
    }

    fun processInteractiveMove(gameInfo: GameInfo, elementId: Int, skillId: Int): Boolean {
        gameInfo.eventStore.clear()
        InteractiveUtil.useInteractive(gameInfo, elementId, skillId)
        waitUntilMoveClickProcessed(gameInfo)
        waitForMapChange(gameInfo)
        return true
    }

    fun processMove(gameInfo: GameInfo, clickLocation: PointRelative): Boolean {
        gameInfo.eventStore.clear()
        MouseUtil.leftClick(gameInfo, clickLocation)
        waitUntilMoveClickProcessed(gameInfo)
        waitForMapChange(gameInfo)
        return true
    }

    private fun waitUntilMoveClickProcessed(gameInfo: GameInfo) {
        WaitUtil.waitUntilMultipleMessagesArrive(
            gameInfo,
            CurrentMapMessage::class.java,
            MapComplementaryInformationsDataMessage::class.java,
            SetCharacterRestrictionsMessage::class.java
        )
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