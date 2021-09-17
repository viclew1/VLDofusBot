package fr.lewon.dofus.bot.scripts.tasks.impl.moves.custom

import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative

class CustomMoveTask(private val location: PointRelative) : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        return MoveUtil.processMove(location).dofusMap
    }

    override fun onStarted(): String {
        return "Moving to point [${location.x}, ${location.y}]"
    }
}