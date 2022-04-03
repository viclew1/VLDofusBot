package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.TravelTask
import fr.lewon.dofus.bot.util.network.GameInfo

class TravelScript : DofusBotScript("Travel") {

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
        return "Does the same as the autopilot mount, except you don't need to have an autopilot mount."
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val x = xParameter.value.toInt()
        val y = yParameter.value.toInt()
        val mapsWithCoords = MapManager.getDofusMaps(x, y)
        val maps = mapsWithCoords.filter {
            it.worldMap == gameInfo.currentMap.worldMap && it.subArea.area.superAreaId == gameInfo.currentMap.subArea.area.superAreaId
        }.takeIf { it.isNotEmpty() }
            ?: mapsWithCoords
        val travelOk = TravelTask(maps).run(logItem, gameInfo)
        if (!travelOk) {
            val destMapsStr = maps.joinToString(", ") { "(${it.posX}; ${it.posY})" }
            error("Failed to reach destinations : $destMapsStr")
        }
    }

}