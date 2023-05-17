package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.map.HintManager
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.d2o.managers.map.WorldMapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.maps.DofusWorldMap
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
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
    private val MAP_ID_BY_DUNGEON = HintManager.getHints(HintManager.HintType.DUNGEON).associate {
        "${it.name} (${it.map.subArea.level})" to it.map
    }
    private val REACH_MAP_TYPE_LABELS = ReachMapType.values().map { it.label }
    private val DUNGEON_LABELS = MAP_ID_BY_DUNGEON.entries.sortedWith(
        compareBy({ it.value.subArea.level }, { it.key })
    ).map { it.key }

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

    private val useCurrentWorldMapParameter = DofusBotParameter(
        "Use current world map",
        "Reach destination coordinates in current world map",
        "false",
        DofusBotParameterType.BOOLEAN,
        displayCondition = { it.getParamValue(reachMapTypeParameter) == ReachMapType.BY_COORDINATES.label }
    )

    private val worldMapParameter = DofusBotParameter(
        "World map",
        "Destination world map",
        DEFAULT_WORLD_MAP_LABEL,
        DofusBotParameterType.CHOICE,
        WORLD_MAPS_LABELS,
        displayCondition = {
            it.getParamValue(reachMapTypeParameter) == ReachMapType.BY_COORDINATES.label
                    && it.getParamValue(useCurrentWorldMapParameter) == false.toString()
        }
    )

    private val dungeonParameter = DofusBotParameter(
        "Dungeon",
        "Dungeon entrance destination",
        DUNGEON_LABELS.first(),
        DofusBotParameterType.CHOICE,
        DUNGEON_LABELS,
        displayCondition = { it.getParamValue(reachMapTypeParameter) == ReachMapType.BY_DUNGEON.label }
    )

    private val useZaapsParameter = DofusBotParameter(
        "Use zaaps",
        "Check if you want to allow zaaps for the travel",
        "true",
        DofusBotParameterType.BOOLEAN
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(
            reachMapTypeParameter,
            mapIdParameter,
            xParameter,
            yParameter,
            useCurrentWorldMapParameter,
            worldMapParameter,
            dungeonParameter,
            useZaapsParameter
        )
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Reaches the destination using zaaps, transporters or NPCs if needed."
    }

    override fun doExecuteScript(logItem: LogItem, gameInfo: GameInfo, scriptValues: ScriptValues) {
        val useZaaps = scriptValues.getParamValue(useZaapsParameter).toBoolean()
        val destMaps = getDestMaps(gameInfo, scriptValues)
        val travelOk = if (useZaaps) {
            ReachMapTask(destMaps).run(logItem, gameInfo)
        } else {
            TravelTask(destMaps).run(logItem, gameInfo)
        }
        if (!travelOk) {
            error("Failed to reach destination")
        }
    }

    private fun getDestMaps(gameInfo: GameInfo, scriptValues: ScriptValues): List<DofusMap> {
        return when (ReachMapType.fromLabel(scriptValues.getParamValue(reachMapTypeParameter))) {
            ReachMapType.BY_COORDINATES -> getDestMapsByCoordinates(gameInfo, scriptValues)
            ReachMapType.BY_MAP_ID -> getDestMapsByMapId(scriptValues)
            ReachMapType.BY_DUNGEON -> getDungeonMaps(scriptValues)
        }
    }

    private fun getDungeonMaps(scriptValues: ScriptValues): List<DofusMap> {
        val dungeon = scriptValues.getParamValue(dungeonParameter)
        val mapId = MAP_ID_BY_DUNGEON[dungeon] ?: error("Couldn't find dungeon map id : $dungeon")
        return listOf(mapId)
    }

    private fun getDestMapsByMapId(scriptValues: ScriptValues): List<DofusMap> {
        val mapId = scriptValues.getParamValue(mapIdParameter).toDouble()
        return listOf(MapManager.getDofusMap(mapId))
    }

    private fun getDestMapsByCoordinates(gameInfo: GameInfo, scriptValues: ScriptValues): List<DofusMap> {
        val x = scriptValues.getParamValue(xParameter).toIntOrNull() ?: error("Invalid X")
        val y = scriptValues.getParamValue(yParameter).toIntOrNull() ?: error("Invalid Y")
        val worldMap = getDestinationWorldMap(gameInfo.currentMap, scriptValues)
        val maps = MapManager.getDofusMaps(x, y)
        val mapsOnWorldMap = maps.filter { it.worldMap == worldMap }
        if (mapsOnWorldMap.isEmpty()) {
            error("Can't find a map with coordinates ($x, $y) in world map [${worldMap.name}]")
        }
        return mapsOnWorldMap.filter { it.hasPriorityOnWorldMap }
            .ifEmpty { mapsOnWorldMap }
    }

    private fun getDestinationWorldMap(currentMap: DofusMap, scriptValues: ScriptValues): DofusWorldMap {
        if (scriptValues.getParamValue(useCurrentWorldMapParameter).toBoolean()) {
            return currentMap.worldMap
                ?: WorldMapManager.getWorldMap(1)
                ?: error("No world map found")
        }
        val worldMapStr = scriptValues.getParamValue(worldMapParameter)
        return WORLD_MAPS_BY_LABEL[worldMapStr]
            ?: error("World map not found : $worldMapStr")
    }

    private enum class ReachMapType(val label: String) {
        BY_COORDINATES("By Coordinates"),
        BY_MAP_ID("By Map ID"),
        BY_DUNGEON("By Dungeon");

        companion object {
            fun fromLabel(label: String): ReachMapType {
                return values().firstOrNull { it.label == label }
                    ?: error("Reach map type does not exist : $label")
            }
        }
    }
}