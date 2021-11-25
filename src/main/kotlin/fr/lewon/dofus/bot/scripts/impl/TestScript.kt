package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
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
        val cellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId] ?: error("")
        val cellData = gameInfo.completeCellDataByCellId[cellId]?.cellData ?: error("")
        println(cellData)
        val cell = gameInfo.dofusBoard.getCell(cellId)
        println("Row : ${cell.row} / Col : ${cell.col}")
        println("Neighbors :")
        cell.neighbors.forEach {
            println("Row : ${it.row} / Col : ${it.col}")
            println(it.cellData)
        }
    }

}