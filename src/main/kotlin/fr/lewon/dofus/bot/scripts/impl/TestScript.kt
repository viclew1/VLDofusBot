package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class TestScript : DofusBotScript("Test") {


    override fun getParameters(): List<DofusBotParameter> {
        return listOf()
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Test script for development only"
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val cellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId] ?: return
        val cell = gameInfo.dofusBoard.getCell(cellId)
        println(cell)
        MoveUtil.buildDirectionalMoveTask(gameInfo, Direction.RIGHT, 5).run(logItem, gameInfo)
        MoveUtil.buildDirectionalMoveTask(gameInfo, Direction.LEFT, 5).run(logItem, gameInfo)
    }

}