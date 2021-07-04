package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.game.move.Direction
import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager
import fr.lewon.dofus.bot.util.geometry.PointRelative

class MoveLeftTask : MoveTask(Direction.LEFT) {

    override fun getMoveDest(): PointRelative {
        return DTBConfigManager.config.leftAccessPos
    }

}