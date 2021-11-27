package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.network.GameInfo

object TestScript : DofusBotScript("Test") {


    override fun getParameters(): List<DofusBotScriptParameter> {
        return listOf()
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Test script for development only"
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken) {
        var currentDirection = Direction.LEFT
        for (i in 0 until 10) {
            MoveUtil.buildMoveTask(currentDirection).run(logItem, gameInfo, cancellationToken)
            currentDirection = currentDirection.getReverseDir()
        }
    }

}