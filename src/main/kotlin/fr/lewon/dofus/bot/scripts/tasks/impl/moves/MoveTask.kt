package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.game.move.MoveHistory
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative

abstract class MoveTask(private val direction: Direction) : DofusBotTask<DofusMap>() {

    override fun onSucceeded(value: DofusMap): String {
        return "OK - [${value.posX},${value.posY}]"
    }

    override fun onStarted(): String {
        return "Moving toward [$direction] ..."
    }

    override fun execute(logItem: LogItem): DofusMap {
        val fromMap = GameInfo.currentMap
        val moveDest = ConfigManager.config.moveAccessStore.getAccessPoint(fromMap.id, direction)
            ?.let { MoveUtil.processMove(it) }
            ?: MoveUtil.processMove(getMoveDest())
        MoveHistory.addMove(direction, fromMap, moveDest.map)
        return moveDest.map
    }

    protected abstract fun getMoveDest(): PointRelative

}