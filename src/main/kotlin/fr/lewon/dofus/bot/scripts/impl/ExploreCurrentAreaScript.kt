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
        "run_forever",
        "Explore this area until you manually stop",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    private val killEverythingParameter = DofusBotParameter(
        "kill_everything",
        "Fight every group of monsters present on the maps",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(runForeverParameter, killEverythingParameter)
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
        val subArea = gameInfo.currentMap.subArea
        val worldMap = gameInfo.currentMap.worldMap
        var success: Boolean
        do {
            success = ExploreSubAreaTask(subArea, worldMap, killEverything).run(logItem, gameInfo)
        } while (success && runForever)
    }

}