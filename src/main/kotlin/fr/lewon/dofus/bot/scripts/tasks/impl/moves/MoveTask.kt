package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.game.move.Direction
import fr.lewon.dofus.bot.game.move.MoveHistory
import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil

abstract class MoveTask(private val direction: Direction) : DofusBotTask<DofusMap>() {

    override fun onSucceeded(value: DofusMap): String {
        return "OK - [${value.posX},${value.posY}]"
    }

    override fun onStarted(): String {
        return "Moving toward [$direction] ..."
    }

    override fun execute(logItem: LogItem): DofusMap {
        val fromMap = GameInfo.currentMap
        val moveDest =
            DTBConfigManager.config.moveAccessStore.getAccessPoint(fromMap.id, direction)
                ?.let { processMove(it) }
                ?: processMove(getMoveDest())
        MoveHistory.addMove(direction, fromMap, moveDest.dofusMap)
        return moveDest.dofusMap
    }

    private fun processMove(moveDest: PointRelative): MapComplementaryInformationsDataMessage {
        MouseUtil.leftClick(moveDest, false, 0)
        return EventStore.waitForEvent(MapComplementaryInformationsDataMessage::class.java)
    }

    protected abstract fun getMoveDest(): PointRelative

}