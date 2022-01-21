package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.d2o.managers.MapManager
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.network.GameInfo

class ReachMapScript : DofusBotScript("Reach map") {

    private val xParameter = DofusBotParameter(
        "x", "X coordinates of destination", "0", DofusBotParameterType.INTEGER
    )

    private val yParameter = DofusBotParameter(
        "y", "Y coordinates of destination", "0", DofusBotParameterType.INTEGER
    )

    override fun getParameters(): List<DofusBotParameter> {
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
        val mapsWithCoords = MapManager.getDofusMaps(x, y)
        val maps = mapsWithCoords.filter { it.worldMap == gameInfo.currentMap.worldMap }
            .takeIf { it.isNotEmpty() }
            ?: mapsWithCoords
        val travelOk = ReachMapTask(maps).run(logItem, gameInfo)
        if (!travelOk) {
            error("Failed to reach destination")
        }
    }

}