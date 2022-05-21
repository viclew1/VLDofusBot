package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.d2o.managers.map.WorldMapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.TravelTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.network.GameInfo

class ReachMapScript : DofusBotScript("Reach map") {

    companion object {
        private val WORLD_MAPS = WorldMapManager.getAllWorldMaps()
            .filter { it.viewableEverywhere && it.visibleOnMap }
        private val WORLD_MAPS_BY_LABEL = WORLD_MAPS.associateBy { it.name }
        private val WORLD_MAPS_LABELS = WORLD_MAPS_BY_LABEL.keys.sorted()
        private val DEFAULT_WORLD_MAP_LABEL = WorldMapManager.getWorldMap(1)?.name
            ?: error("Default world map not found")
    }

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

    private val worldMapParameter = DofusBotParameter(
        "World map", "Destination world map", DEFAULT_WORLD_MAP_LABEL, DofusBotParameterType.CHOICE, WORLD_MAPS_LABELS
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(
            xParameter,
            yParameter,
            worldMapParameter,
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
        val worldMap = WORLD_MAPS_BY_LABEL[worldMapParameter.value]
            ?: error("World map not found : ${worldMapParameter.value}")
        val maps = MapManager.getDofusMaps(x, y)
        val mapsOnWorldMap = maps.filter { it.worldMap == worldMap }
        if (mapsOnWorldMap.isEmpty()) {
            error("Can't find a map with coordinates ($x, $y) in world map ${worldMapParameter.value}")
        }
        val destMaps = mapsOnWorldMap.filter { it.hasPriorityOnWorldMap }
            .takeIf { it.isNotEmpty() }
            ?: mapsOnWorldMap
        val travelOk = if (useTeleports) {
            ReachMapTask(destMaps).run(logItem, gameInfo)
        } else {
            TravelTask(destMaps).run(logItem, gameInfo)
        }
        if (!travelOk) {
            error("Failed to reach destination")
        }
    }

}