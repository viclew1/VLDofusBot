package fr.lewon.dofus.bot.scripts.tasks.impl.complex

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.game.MoveUtil

class MultimapPhorrorSearchTask(private val direction: Direction, private val alreadyFoundPos: List<DofusMap>) :
    DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        for (moveCount in 0 until 10) {
            MoveUtil.buildMoveTask(direction).run(logItem)
            if (!alreadyFoundPos.contains(GameInfo.currentMap) && GameInfo.phorrorOnMap) {
                return GameInfo.currentMap
            }
        }
        error("No phorror found")
    }

    override fun onSucceeded(value: DofusMap): String {
        return "Found : [${value.posX},${value.posY}]"
    }

    override fun onStarted(): String {
        return "Looking for a phorror in the [$direction] direction ..."
    }
}