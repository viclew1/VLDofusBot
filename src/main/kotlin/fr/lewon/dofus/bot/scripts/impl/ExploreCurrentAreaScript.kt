package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.ExploreSubAreaTask
import fr.lewon.dofus.bot.util.network.GameInfo

class ExploreCurrentAreaScript : DofusBotScript("Explore current area") {


    override fun getParameters(): List<DofusBotParameter> {
        return listOf()
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Explore all maps of current sub area"
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val subArea = gameInfo.currentMap.subArea
        val worldMap = gameInfo.currentMap.worldMap
        ExploreSubAreaTask(subArea, worldMap).run(logItem, gameInfo)
    }

}