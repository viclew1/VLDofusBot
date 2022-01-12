package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.d2o.managers.MapManager
import fr.lewon.dofus.bot.game.move.transporters.TravelUtil
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MoveTask
import fr.lewon.dofus.bot.util.network.GameInfo

class ReachHavenBagTask : AbstractHavenBagTask(true) {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        while (!super.doExecute(logItem, gameInfo)) {
            val lastMap = gameInfo.moveHistory.pollLastMap() ?: return false
            val transitions = TravelUtil.getPath(gameInfo, MapManager.getDofusMap(lastMap)) ?: return false
            if (!MoveTask(transitions).run(logItem, gameInfo)) {
                return false
            }
            gameInfo.moveHistory.pollLastMap()
        }
        return true
    }

    override fun onStarted(): String {
        return "Reaching haven bag ..."
    }
}