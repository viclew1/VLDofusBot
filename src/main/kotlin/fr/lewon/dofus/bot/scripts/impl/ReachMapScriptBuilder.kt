package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.d2o.managers.map.WorldMapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.model.characters.VldbScriptValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.TravelTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.network.info.GameInfo

object ReachMapScriptBuilder : DofusBotScriptBuilder("Reach map") {

    private val WORLD_MAPS = WorldMapManager.getAllWorldMaps()
        .filter { it.viewableEverywhere && it.visibleOnMap }
    private val WORLD_MAPS_BY_LABEL = WORLD_MAPS.associateBy { it.name }
    private val WORLD_MAPS_LABELS = WORLD_MAPS_BY_LABEL.keys.sorted()
    private val DEFAULT_WORLD_MAP_LABEL = WorldMapManager.getWorldMap(1)?.name
        ?: error("Default world map not found")

    private val REACH_MAP_TYPE_LABELS = ReachMapType.values().map { it.label }

    private val reachMapTypeParameter = DofusBotParameter(
        "Type",
        "How to retrieve the destination map",
        ReachMapType.BY_COORDINATES.label,
        DofusBotParameterType.CHOICE,
        REACH_MAP_TYPE_LABELS,
    )

    private val mapIdParameter = DofusBotParameter(
        "Map ID",
        "Map ID of destination",
        "0",
        DofusBotParameterType.INTEGER,
        displayCondition = { it.getParamValue(reachMapTypeParameter) == ReachMapType.BY_MAP_ID.label }
    )

    private val xParameter = DofusBotParameter(
        "x",
        "X coordinates of destination",
        "0",
        DofusBotParameterType.INTEGER,
        displayCondition = { it.getParamValue(reachMapTypeParameter) == ReachMapType.BY_COORDINATES.label }
    )

    private val yParameter = DofusBotParameter(
        "y",
        "Y coordinates of destination",
        "0",
        DofusBotParameterType.INTEGER,
        displayCondition = { it.getParamValue(reachMapTypeParameter) == ReachMapType.BY_COORDINATES.label }
    )

    private val worldMapParameter = DofusBotParameter(
        "World map",
        "Destination world map",
        DEFAULT_WORLD_MAP_LABEL,
        DofusBotParameterType.CHOICE,
        WORLD_MAPS_LABELS,
        displayCondition = { it.getParamValue(reachMapTypeParameter) == ReachMapType.BY_COORDINATES.label }
    )

    private val useTeleportsParameter = DofusBotParameter(
        "Use teleports",
        "Check if you want to allow teleports for the travel",
        "true",
        DofusBotParameterType.BOOLEAN
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(
            reachMapTypeParameter,
            mapIdParameter,
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

    override fun doExecuteScript(logItem: LogItem, gameInfo: GameInfo, scriptValues: VldbScriptValues) {
        val useTeleports = scriptValues.getParamValue(useTeleportsParameter).toBoolean()
        val destMaps = getDestMaps(scriptValues)
        val travelOk = if (useTeleports) {
            ReachMapTask(destMaps).run(logItem, gameInfo)
        } else {
            TravelTask(destMaps).run(logItem, gameInfo)
        }
        if (!travelOk) {
            error("Failed to reach destination")
        }
    }

    private fun getDestMaps(scriptValues: VldbScriptValues): List<DofusMap> {
        return when (ReachMapType.fromLabel(scriptValues.getParamValue(reachMapTypeParameter))) {
            ReachMapType.BY_COORDINATES -> getDestMapsByCoordinates(scriptValues)
            ReachMapType.BY_MAP_ID -> getDestMapsByMapId(scriptValues)
        }
    }

    private fun getDestMapsByMapId(scriptValues: VldbScriptValues): List<DofusMap> {
        val mapId = scriptValues.getParamValue(mapIdParameter).toDouble()
        return listOf(MapManager.getDofusMap(mapId))
    }

    private fun getDestMapsByCoordinates(scriptValues: VldbScriptValues): List<DofusMap> {
        val x = scriptValues.getParamValue(xParameter).toIntOrNull() ?: error("Invalid X")
        val y = scriptValues.getParamValue(yParameter).toIntOrNull() ?: error("Invalid Y")
        val worldMapStr = scriptValues.getParamValue(worldMapParameter)
        val worldMap = WORLD_MAPS_BY_LABEL[worldMapStr]
            ?: error("World map not found : $worldMapStr")
        val maps = MapManager.getDofusMaps(x, y)
        val mapsOnWorldMap = maps.filter { it.worldMap == worldMap }
        if (mapsOnWorldMap.isEmpty()) {
            error("Can't find a map with coordinates ($x, $y) in world map $worldMapStr")
        }
        return mapsOnWorldMap.filter { it.hasPriorityOnWorldMap }
            .takeIf { it.isNotEmpty() }
            ?: mapsOnWorldMap
    }

    private enum class ReachMapType(val label: String) {
        BY_COORDINATES("By Coordinates"),
        BY_MAP_ID("By Map ID");

        companion object {
            fun fromLabel(label: String): ReachMapType {
                return values().firstOrNull { it.label == label }
                    ?: error("Reach map type does not exist : $label")
            }
        }
    }
}