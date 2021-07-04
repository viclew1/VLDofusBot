package fr.lewon.dofus.bot.scripts.tasks.impl.complex

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.game.move.Direction
import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.ui.DTBLogger

class MultimapMoveTask(private val direction: Direction, private val dist: Int) : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        for (i in 0 until dist) {
            DTBLogger.closeLog("Moves done : $i/$dist", logItem)
            direction.buildMoveTask().run(logItem)
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