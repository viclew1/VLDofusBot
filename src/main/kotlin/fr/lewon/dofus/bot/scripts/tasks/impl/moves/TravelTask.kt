package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.world.Transition
import fr.lewon.dofus.bot.core.manager.world.TransitionType
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.game.move.transporters.TravelUtil
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.custom.CellMoveTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.custom.InteractiveMoveTask
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.network.GameInfo

open class TravelTask(private val destMaps: List<DofusMap>) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (destMaps.contains(gameInfo.currentMap)) {
            return true
        }
        val path = TravelUtil.getPath(gameInfo, destMaps) ?: error("Travel path not found")
        for (i in path.indices) {
            gameInfo.logger.closeLog("Moves done : $i/${path.size}", logItem)
            if (!processTransition(logItem, gameInfo, path[i])) {
                return false
            }
        }
        return true
    }

    private fun processTransition(
        logItem: LogItem,
        gameInfo: GameInfo,
        transition: Transition
    ): Boolean {
        return when (transition.type) {
            TransitionType.SCROLL, TransitionType.SCROLL_ACTION ->
                MoveUtil.buildMoveTask(Direction.fromInt(transition.direction), transition.cellId)
                    .run(logItem, gameInfo)
            TransitionType.MAP_ACTION ->
                CellMoveTask(transition.cellId).run(logItem, gameInfo)
            TransitionType.INTERACTIVE ->
                InteractiveMoveTask(transition.id.toInt()).run(logItem, gameInfo)
            else -> error("Transition not implemented yet : ${transition.type}")
        }
    }

    override fun onStarted(): String {
        val mapsStr = destMaps.map { it.getCoordinates() }
            .distinct()
            .joinToString(", ") { "(${it.x}; ${it.y})" }
        return "Traveling to maps : [$mapsStr] ..."
    }

}