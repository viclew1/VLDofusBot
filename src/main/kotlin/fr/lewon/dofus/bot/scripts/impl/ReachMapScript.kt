package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.TravelTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.network.GameInfo

class ReachMapScript : DofusBotScript("Reach map") {

    private val xParameter = DofusBotParameter(
        "x", "X coordinates of destination", "0", DofusBotParameterType.INTEGER
    )

    private val yParameter = DofusBotParameter(
        "y", "Y coordinates of destination", "0", DofusBotParameterType.INTEGER
    )

    private val useTeleportsParameter = DofusBotParameter(
        "Use teleports",
        "Check if you want to allow teleports for the travel",
        "true",
        DofusBotParameterType.BOOLEAN
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(
            xParameter,
            yParameter,
            useTeleportsParameter
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
        val useTeleports = useTeleportsParameter.value.toBoolean()
        val maps = MapManager.getDofusMaps(x, y)
        val destMaps = getMapsInSubArea(gameInfo, maps)
            ?: getMapsInArea(gameInfo, maps)
            ?: getMapsInSuperArea(gameInfo, maps)
            ?: maps
        val travelOk = if (useTeleports) {
            ReachMapTask(destMaps).run(logItem, gameInfo)
        } else {
            TravelTask(destMaps).run(logItem, gameInfo)
        }
        if (!travelOk) {
            error("Failed to reach destination")
        }
    }

    private fun getMapsInSubArea(gameInfo: GameInfo, maps: List<DofusMap>): List<DofusMap>? {
        return maps.filter { it.subArea == gameInfo.currentMap.subArea }
            .takeIf { it.isNotEmpty() }
    }

    private fun getMapsInArea(gameInfo: GameInfo, maps: List<DofusMap>): List<DofusMap>? {
        return maps.filter { it.subArea.area == gameInfo.currentMap.subArea.area }
            .takeIf { it.isNotEmpty() }
    }

    private fun getMapsInSuperArea(gameInfo: GameInfo, maps: List<DofusMap>): List<DofusMap>? {
        return maps.filter { it.subArea.area.superAreaId == gameInfo.currentMap.subArea.area.superAreaId }
            .takeIf { it.isNotEmpty() }
    }

}