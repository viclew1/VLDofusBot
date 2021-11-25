package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.manager.world.Transition
import fr.lewon.dofus.bot.core.manager.world.TransitionType
import fr.lewon.dofus.bot.core.manager.world.WorldGraphUtil
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.custom.CellMoveTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.custom.InteractiveMoveTask
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.network.GameInfo

open class TravelTask(private val destMaps: List<DofusMap>) : BooleanDofusBotTask() {

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        if (destMaps.contains(gameInfo.currentMap)) {
            return true
        }
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Couldn't find player position on map")
        val cellData = gameInfo.completeCellDataByCellId[playerCellId]?.cellData
            ?: error("Couldn't find cell data information for cell : $playerCellId")
        val path = WorldGraphUtil.getPath(gameInfo.currentMap, cellData.getLinkedZoneRP(), destMaps)
        if (path == null) {
            VldbLogger.info("Travel path not found", logItem)
            return false
        }
        for (i in path.indices) {
            VldbLogger.closeLog("Moves done : $i/${path.size}", logItem)
            if (!processTransition(logItem, gameInfo, cancellationToken, path[i])) {
                return false
            }
        }

        return true
    }

    private fun processTransition(
        logItem: LogItem,
        gameInfo: GameInfo,
        cancellationToken: CancellationToken,
        transition: Transition
    ): Boolean {
        return when (transition.type) {
            TransitionType.SCROLL, TransitionType.SCROLL_ACTION ->
                MoveUtil.buildMoveTask(Direction.fromInt(transition.direction), transition.cellId)
                    .run(logItem, gameInfo, cancellationToken)
            TransitionType.MAP_ACTION ->
                CellMoveTask(transition.cellId).run(logItem, gameInfo, cancellationToken)
            TransitionType.INTERACTIVE ->
                InteractiveMoveTask(transition.id.toInt()).run(logItem, gameInfo, cancellationToken)
            else -> error("Transition not implemented yet : ${transition.type}")
        }
    }

    override fun onStarted(): String {
        return "Traveling to maps : [${destMaps.map { it.id }.joinToString(" / ")}] ..."
    }

}