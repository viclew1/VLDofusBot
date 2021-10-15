package fr.lewon.dofus.bot.scripts.tasks.impl.complex

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.game.MoveUtil

class MultimapMoveTask(private val direction: Direction, private val dist: Int) : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        for (i in 0 until dist) {
            VldbLogger.closeLog("Moves done : $i/$dist", logItem)
            MoveUtil.buildMoveTask(direction).run(logItem)
        }
        return GameInfo.currentMap
    }

    override fun onSucceeded(value: DofusMap): String {
        return "OK : [${value.posX},${value.posY}]"
    }

    override fun onStarted(): String {
        return "Moving [$dist] cells in the [$direction] direction ..."
    }

}