package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.ExploreSubAreaTask
import fr.lewon.dofus.bot.util.network.GameInfo

class ExploreCurrentAreaScript : DofusBotScript("Explore current area") {

    private val runForeverParameter = DofusBotParameter(
        "Run forever",
        "Explore this area until you manually stop",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    private val killEverythingParameter = DofusBotParameter(
        "Kill everything",
        "Fight every group of monsters present on the maps",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    private val searchedMonsterParameter = DofusBotParameter(
        "Searched monster",
        "Monster which will stop exploration when found. Leave empty if you're not seeking a monster",
        "",
        DofusBotParameterType.STRING
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(runForeverParameter, killEverythingParameter, searchedMonsterParameter)
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Explore all maps of current sub area"
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val runForever = runForeverParameter.value.toBoolean()
        val killEverything = killEverythingParameter.value.toBoolean()
        val searchedMonsterName = searchedMonsterParameter.value
        val subArea = gameInfo.currentMap.subArea
        val worldMap = gameInfo.currentMap.worldMap
        var success: Boolean
        do {
            success = ExploreSubAreaTask(subArea, worldMap, killEverything, searchedMonsterName).run(logItem, gameInfo)
        } while (success && runForever)
    }

}