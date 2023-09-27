package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusCoordinates
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.core.world.Transition
import fr.lewon.dofus.bot.core.world.TransitionType
import fr.lewon.dofus.bot.core.world.Vertex
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.GameCautiousMapMovementRequestMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.GameMapMovementRequestMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.ChangeMapMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.CurrentMapMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.MapInformationsRequestMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.initialization.SetCharacterRestrictionsMessage
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

object MoveUtil {

    fun getInvalidCells(gameInfo: GameInfo): List<DofusCell> =
        gameInfo.entityPositionsOnMapByEntityId.values
            .flatMap { getInvalidCellsNearEntity(gameInfo, it) }

    fun getInvalidCellsNearEntity(gameInfo: GameInfo, entityCellId: Int): List<DofusCell> {
        val entityCell = gameInfo.dofusBoard.getCell(entityCellId)
        val invalidCells = mutableListOf(entityCell)
        val row = entityCell.row
        val col = entityCell.col
        for (i in 0 until 4) {
            gameInfo.dofusBoard.getCell(col - 1 - i, row - i)?.let { invalidCells.add(it) }
            gameInfo.dofusBoard.getCell(col - i, row - 1 - i)?.let { invalidCells.add(it) }
            gameInfo.dofusBoard.getCell(col - 1 - i, row - 1 - i)?.let { invalidCells.add(it) }
        }
        gameInfo.dofusBoard.getCell(col + 1, row)?.let { invalidCells.add(it) }
        gameInfo.dofusBoard.getCell(col, row + 1)?.let { invalidCells.add(it) }
        return invalidCells
    }

    fun buildDirectionalPath(gameInfo: GameInfo, direction: Direction, amount: Int): List<Transition>? =
        buildDirectionalPath(gameInfo, direction, { _, count -> count == amount }, amount)

    fun buildDirectionalPath(
        gameInfo: GameInfo, direction: Direction, stopFunc: (DofusMap, Int) -> Boolean, limit: Int,
    ): List<Transition>? = buildDirectionalPath(TravelUtil.getCurrentVertex(gameInfo), direction, stopFunc, limit)

    fun buildDirectionalPath(
        startVertex: Vertex, direction: Direction, stopFunc: (DofusMap, Int) -> Boolean, limit: Int,
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

        val potentialTransitions = getPotentialDirectionalTransitions(direction, specialTransitions, fromMap)
        return getClosestTransition(potentialTransitions, getIdealDestination(direction, fromMap))
            ?: error("No path found")
    }

    private fun getIdealDestination(direction: Direction, fromMap: DofusMap): DofusCoordinates = when (direction) {
        Direction.LEFT -> DofusCoordinates(fromMap.posX - 1, fromMap.posY)
        Direction.RIGHT -> DofusCoordinates(fromMap.posX + 1, fromMap.posY)
        Direction.TOP -> DofusCoordinates(fromMap.posX, fromMap.posY - 1)
        Direction.BOTTOM -> DofusCoordinates(fromMap.posX, fromMap.posY + 1)
    }

    private fun getPotentialDirectionalTransitions(
        direction: Direction,
        transitions: List<Transition>,
        fromMap: DofusMap,
    ): List<Transition> = transitions.filter {
        val destMapCoordinates = MapManager.getDofusMap(it.edge.to.mapId).coordinates
        when (direction) {
            Direction.LEFT -> destMapCoordinates.x < fromMap.coordinates.x
            Direction.RIGHT -> destMapCoordinates.x > fromMap.coordinates.x
            Direction.TOP -> destMapCoordinates.y < fromMap.coordinates.y
            Direction.BOTTOM -> destMapCoordinates.y > fromMap.coordinates.y
        }
    }

    private fun getClosestTransition(transitions: List<Transition>, targetCoordinates: DofusCoordinates): Transition? {
        return transitions.minByOrNull {
            MapManager.getDofusMap(it.edge.to.mapId).coordinates.distanceTo(targetCoordinates)
        }
    }

    fun processCellMove(gameInfo: GameInfo, cellId: Int): Boolean =
        processMove(gameInfo) { InteractiveUtil.getCellClickPosition(gameInfo, cellId) }

    fun processInteractiveMove(gameInfo: GameInfo, elementId: Int, skillId: Int): Boolean {
        gameInfo.eventStore.clear()
        InteractiveUtil.useInteractive(gameInfo, elementId, skillId)
        waitForMapChangeFinished(gameInfo)
        return true
    }

    fun processMove(gameInfo: GameInfo, getClickLocation: () -> PointRelative): Boolean {
        gameInfo.eventStore.clear()
        clickUntilMapChangeRequested(gameInfo, getClickLocation)
        waitForMapChangeFinished(gameInfo)
        return true
    }

    private fun clickUntilMapChangeRequested(gameInfo: GameInfo, getClickLocation: () -> PointRelative) {
        RetryUtil.tryUntilSuccess(
            { MouseUtil.leftClick(gameInfo, getClickLocation()) },
            { waitUntilMapChangeRequested(gameInfo) },
            4,
        ) ?: error("No map change requested")
    }

    private fun waitUntilMapChangeRequested(gameInfo: GameInfo): Boolean = WaitUtil.waitUntil(1500) {
        gameInfo.eventStore.getLastEvent(ChangeMapMessage::class.java) != null
            || gameInfo.eventStore.getLastEvent(GameMapMovementRequestMessage::class.java) != null
            || gameInfo.eventStore.getLastEvent(GameCautiousMapMovementRequestMessage::class.java) != null
            || gameInfo.eventStore.getLastEvent(MapInformationsRequestMessage::class.java) != null
    }

    fun isMapChanged(
        gameInfo: GameInfo,
        complementaryInformationClass: Class<out MapComplementaryInformationsDataMessage> = MapComplementaryInformationsDataMessage::class.java,
    ): Boolean = gameInfo.eventStore.getAllEvents(SetCharacterRestrictionsMessage::class.java).isNotEmpty()
        && gameInfo.eventStore.getAllEvents(CurrentMapMessage::class.java).isNotEmpty()
        && gameInfo.eventStore.getAllEvents(complementaryInformationClass).isNotEmpty()

    fun waitForMapChangeFinished(
        gameInfo: GameInfo,
        complementaryInformationClass: Class<out MapComplementaryInformationsDataMessage> = MapComplementaryInformationsDataMessage::class.java,
    ) {
        WaitUtil.waitForEvents(
            gameInfo,
            complementaryInformationClass,
            CurrentMapMessage::class.java,
            SetCharacterRestrictionsMessage::class.java,
        )
        gameInfo.eventStore.clearUntilLast(CurrentMapMessage::class.java)
        WaitUtil.waitForEvents(gameInfo, SetCharacterRestrictionsMessage::class.java)
    }

}