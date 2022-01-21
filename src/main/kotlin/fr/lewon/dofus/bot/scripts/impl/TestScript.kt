package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.criterion.DofusCriterionParser
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
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
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Can't find player cell id")
        val cellData = gameInfo.completeCellDataByCellId[playerCellId]?.cellData
            ?: error("Can't find player cell data")
        val vertex = WorldGraphUtil.getVertex(gameInfo.currentMap.id, cellData.getLinkedZoneRP())
            ?: error("No vertex found")

        WorldGraphUtil.getOutgoingEdges(vertex).flatMap { it.transitions }
            .map { it to it.criterion }
            .filter { it.second.isNotEmpty() }
            .distinct()
            .forEach {
                println(
                    "${it.first} - ${
                        DofusCriterionParser.parse(it.second).check(gameInfo.buildCharacterBasicInfo())
                    }"
                )
            }
    }

}