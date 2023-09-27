package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.map.HintManager
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.d2o.managers.map.WorldMapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.maps.DofusWorldMap
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.BooleanParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.ChoiceParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.IntParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.LongParameter
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.game.TravelUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

object ReachMapScriptBuilder : DofusBotScriptBuilder("Reach map") {

    private val WORLD_MAPS = WorldMapManager.getAllWorldMaps()
        .filter { it.viewableEverywhere && it.visibleOnMap }
    private val DEFAULT_WORLD_MAP = WorldMapManager.getWorldMap(1)
        ?: error("Default world map not found")
    private val MAP_ID_BY_DUNGEON = HintManager.getHints(HintManager.HintType.DUNGEON)
        .associateBy { "${it.name} (${it.level})" }
    private val DUNGEON_LABELS = MAP_ID_BY_DUNGEON.entries.sortedWith(
        compareBy({ it.value.level }, { it.key })
    ).map { it.key }

    val reachMapTypeParameter = ChoiceParameter(
        "Type",
        "How to retrieve the destination map",
        ReachMapType.BY_COORDINATES,
        getAvailableValues = { ReachMapType.entries },
        parametersGroup = 1,
        itemValueToString = { it.label },
        stringToItemValue = { ReachMapType.fromLabel(it) }
    )

    val mapIdParameter = LongParameter(
        "Map ID",
        "Map ID of destination",
        0,
        displayCondition = { it.getParamValue(reachMapTypeParameter) == ReachMapType.BY_MAP_ID },
        parametersGroup = 1
    )

    private val xParameter = IntParameter(
        "x",
        "X coordinates of destination",
        0,
        displayCondition = { it.getParamValue(reachMapTypeParameter) == ReachMapType.BY_COORDINATES },
        parametersGroup = 1
    )

    private val yParameter = IntParameter(
        "y",
        "Y coordinates of destination",
        0,
        displayCondition = { it.getParamValue(reachMapTypeParameter) == ReachMapType.BY_COORDINATES },
        parametersGroup = 1
    )

    private val useCurrentWorldMapParameter = BooleanParameter(
        "Use current world map",
        "Reach destination coordinates in current world map",
        false,
        displayCondition = { it.getParamValue(reachMapTypeParameter) == ReachMapType.BY_COORDINATES },
        parametersGroup = 1
    )

    private val worldMapParameter = ChoiceParameter(
        "World map",
        "Destination world map",
        DEFAULT_WORLD_MAP,
        getAvailableValues = { WORLD_MAPS.sortedBy { it.name } },
        displayCondition = {
            it.getParamValue(reachMapTypeParameter) == ReachMapType.BY_COORDINATES
                && !it.getParamValue(useCurrentWorldMapParameter)
        },
        parametersGroup = 1,
        itemValueToString = { it.name },
        stringToItemValue = {
            WORLD_MAPS.firstOrNull { worldMap -> worldMap.name == it } ?: error("World map not found : $it")
        }
    )

    private val dungeonParameter = ChoiceParameter(
        "Dungeon",
        "Dungeon entrance destination",
        DUNGEON_LABELS.first(),
        getAvailableValues = { DUNGEON_LABELS },
        displayCondition = { it.getParamValue(reachMapTypeParameter) == ReachMapType.BY_DUNGEON },
        parametersGroup = 1,
        itemValueToString = { it },
        stringToItemValue = { it }
    )

    private val useZaapsParameter = BooleanParameter(
        "Use zaaps",
        "Check if you want to allow zaaps for the travel",
        true,
        parametersGroup = 2
    )

    override fun getParameters(): List<DofusBotParameter<*>> {
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

    override fun getDefaultStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Reaches the destination using zaaps, transporters or NPCs if needed."
    }

    override fun doExecuteScript(
        logItem: LogItem,
        gameInfo: GameInfo,
        parameterValues: ParameterValues,
        statValues: HashMap<DofusBotScriptStat, String>,
    ) {
        val useZaaps = parameterValues.getParamValue(useZaapsParameter)
        val availableZaaps = if (useZaaps) TravelUtil.getAllZaapMaps() else emptyList()
        val destMaps = getDestMaps(gameInfo, parameterValues)
        if (!ReachMapTask(destMaps, availableZaaps).run(logItem, gameInfo)) {
            error("Failed to reach destination")
        }
    }

    private fun getDestMaps(gameInfo: GameInfo, parameterValues: ParameterValues): List<DofusMap> {
        return when (parameterValues.getParamValue(reachMapTypeParameter)) {
            ReachMapType.BY_COORDINATES -> getDestMapsByCoordinates(gameInfo, parameterValues)
            ReachMapType.BY_MAP_ID -> getDestMapsByMapId(parameterValues)
            ReachMapType.BY_DUNGEON -> getDungeonMaps(parameterValues)
        }
    }

    private fun getDungeonMaps(parameterValues: ParameterValues): List<DofusMap> {
        val dungeon = parameterValues.getParamValue(dungeonParameter)
        val map = MAP_ID_BY_DUNGEON[dungeon]?.map ?: error("Couldn't find dungeon map id : $dungeon")
        return listOf(map)
    }

    private fun getDestMapsByMapId(parameterValues: ParameterValues): List<DofusMap> {
        val mapId = parameterValues.getParamValue(mapIdParameter)
        return listOf(MapManager.getDofusMap(mapId.toDouble()))
    }

    private fun getDestMapsByCoordinates(gameInfo: GameInfo, parameterValues: ParameterValues): List<DofusMap> {
        val x = parameterValues.getParamValue(xParameter)
        val y = parameterValues.getParamValue(yParameter)
        val worldMap = getDestinationWorldMap(gameInfo.currentMap, parameterValues)
        val maps = MapManager.getDofusMaps(x, y)
        val mapsOnWorldMap = maps.filter { it.worldMap == worldMap }
        if (mapsOnWorldMap.isEmpty()) {
            error("Can't find a map with coordinates ($x, $y) in world map [${worldMap.name}]")
        }
        return mapsOnWorldMap.filter { it.hasPriorityOnWorldMap }
            .ifEmpty { mapsOnWorldMap }
    }

    private fun getDestinationWorldMap(currentMap: DofusMap, parameterValues: ParameterValues): DofusWorldMap {
        return if (parameterValues.getParamValue(useCurrentWorldMapParameter)) {
            currentMap.worldMap
                ?: WorldMapManager.getWorldMap(1)
                ?: error("No world map found")
        } else parameterValues.getParamValue(worldMapParameter)
    }

    enum class ReachMapType(val label: String) {
        BY_COORDINATES("By Coordinates"),
        BY_MAP_ID("By Map ID"),
        BY_DUNGEON("By Dungeon");

        companion object {

            fun fromLabel(label: String): ReachMapType {
                return entries.firstOrNull { it.label == label }
                    ?: error("Reach map type does not exist : $label")
            }
        }
    }
}