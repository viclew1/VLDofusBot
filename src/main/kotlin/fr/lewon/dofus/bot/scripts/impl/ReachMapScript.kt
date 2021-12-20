package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.DofusMapManager
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameterType
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.network.GameInfo

class ReachMapScript : DofusBotScript("Reach map") {

    private val xParameter = DofusBotScriptParameter(
        "x", "X coordinates of destination", "0", DofusBotScriptParameterType.INTEGER
    )

    private val yParameter = DofusBotScriptParameter(
        "y", "Y coordinates of destination", "0", DofusBotScriptParameterType.INTEGER
    )

    override fun getParameters(): List<DofusBotScriptParameter> {
        return listOf(
            xParameter,
            yParameter
        )
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Reaches the destination using zaaps or transporters if needed."
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val x = xParameter.value.toInt()
        val y = yParameter.value.toInt()
        val maps = DofusMapManager.getDofusMaps(x, y).filter { it.worldMap == gameInfo.currentMap.worldMap }
        val travelOk = ReachMapTask(maps).run(logItem, gameInfo)
        if (!travelOk) {
            error("Failed to reach destination")
        }
    }

}