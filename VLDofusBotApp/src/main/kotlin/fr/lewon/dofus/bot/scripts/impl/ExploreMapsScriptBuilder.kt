package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.d2o.managers.map.SubAreaManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.*
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.path.ExploreSubPathsTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.subarea.ExploreSubAreaTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.subarea.ExploreSubAreasTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.util.ExplorationParameters
import fr.lewon.dofus.bot.util.StringUtil
import fr.lewon.dofus.bot.util.filemanagers.impl.MapsPathsManager
import fr.lewon.dofus.bot.util.network.info.GameInfo

object ExploreMapsScriptBuilder : DofusBotScriptBuilder("Explore maps") {

    private val SUB_AREAS = SubAreaManager.getAllSubAreas()
        .filter {
            ExploreSubAreaTask.SUB_AREA_ID_FULLY_ALLOWED.contains(it.id)
                || MapManager.getDofusMaps(it).isNotEmpty()
                && it.area.superAreaId == 0
                && hasNoBoss(it)
        }

    private fun hasNoBoss(subArea: DofusSubArea): Boolean {
        return subArea.monsters.none { it.isBoss }
    }

    private val SUB_AREA_BY_LABEL = SUB_AREAS.associateBy { it.label }

    val explorationTypeParameter = ChoiceParameter(
        "Exploration type",
        "The way to retrieve the maps to explore",
        ExplorationType.SubArea,
        parametersGroup = 1,
        getAvailableValues = { ExplorationType.entries },
        itemValueToString = { it.strValue },
        stringToItemValue = { ExplorationType.entries.first { type -> type.strValue == it } }
    )

    val currentAreaParameter = BooleanParameter(
        "Run in current area",
        "If checked, ignores sub area parameter and explores current area",
        false,
        displayCondition = { it.getParamValue(explorationTypeParameter) == ExplorationType.SubArea },
        parametersGroup = 1
    )

    val subAreasParameter = MultiChoiceParameter(
        "Sub areas",
        "Sub areas to explore",
        listOf(SUB_AREA_BY_LABEL.values.first()),
        getAvailableValues = {
            SUB_AREA_BY_LABEL.entries.sortedBy { StringUtil.removeAccents(it.key) }.map { it.value }
        },
        displayCondition = {
            !it.getParamValue(currentAreaParameter) && it.getParamValue(explorationTypeParameter) == ExplorationType.SubArea
        },
        itemValueToString = { it.label },
        stringToItemValue = { SUB_AREA_BY_LABEL[it] ?: error("Sub area not found : $it") },
        parametersGroup = 1
    )

    val pathParameter = ChoiceParameter(
        "Path",
        "The path to follow",
        defaultValue = null,
        displayCondition = { it.getParamValue(explorationTypeParameter) == ExplorationType.Path },
        getAvailableValues = { MapsPathsManager.getPathByName().values.toList() },
        itemValueToString = { it?.name ?: "" },
        stringToItemValue = { MapsPathsManager.getPathByName()[it] },
        parametersGroup = 1,
    )

    val killEverythingParameter = BooleanParameter(
        "Kill everything",
        "Fights every group of monsters present on the maps",
        false,
        parametersGroup = 2
    )

    val maxMonsterGroupLevelParameter = IntParameter(
        "Max monster group level",
        "Avoid monster groups above this level. 0 to ignore",
        0,
        parametersGroup = 2,
        displayCondition = { it.getParamValue(killEverythingParameter) }
    )

    val maxMonsterGroupSizeParameter = IntParameter(
        "Max monster group size",
        "Avoid monster groups bigger than this size. 0 to ignore",
        0,
        parametersGroup = 2,
        displayCondition = { it.getParamValue(killEverythingParameter) }
    )

    val searchedMonsterParameter = StringParameter(
        "Searched monster",
        "Monster which will stop exploration when found. Leave empty if you're not seeking a monster",
        "",
        parametersGroup = 3
    )

    val stopWhenArchMonsterFoundParameter = BooleanParameter(
        "Stop when arch monster found",
        "Stops exploration when you find an arch monster",
        true,
        parametersGroup = 3
    )

    val stopWhenQuestMonsterFoundParameter = BooleanParameter(
        "Stop when quest monster found",
        "Stops exploration when you find a quest monster",
        false,
        parametersGroup = 3
    )

    val runForeverParameter = BooleanParameter(
        "Run forever",
        "Explores this area until you manually stop",
        false,
        parametersGroup = 4
    )

    val ignoreMapsExploredRecentlyParameter = IntParameter(
        "Ignore maps you explored in the last X min. (0 to explore all)",
        "Ignore maps any of your character explored less than the passed value (in minutes). Set to 0 or less to ignore.",
        15,
        parametersGroup = 4,
        displayCondition = { !it.getParamValue(runForeverParameter) }
    )

    val useZaapsParameter = BooleanParameter(
        "Use zaaps",
        "Check if you want to allow zaaps for the travel",
        true,
        parametersGroup = 5
    )

    override fun getParameters(): List<DofusBotParameter<*>> = listOf(
        explorationTypeParameter,
        currentAreaParameter,
        subAreasParameter,
        pathParameter,
        stopWhenArchMonsterFoundParameter,
        stopWhenQuestMonsterFoundParameter,
        searchedMonsterParameter,
        runForeverParameter,
        killEverythingParameter,
        maxMonsterGroupLevelParameter,
        maxMonsterGroupSizeParameter,
        ignoreMapsExploredRecentlyParameter,
        useZaapsParameter,
    )

    override fun getDefaultStats(): List<DofusBotScriptStat> {
        return emptyList()
    }

    override fun getDescription(): String {
        return "Explore all selected maps"
    }

    override fun doExecuteScript(
        logItem: LogItem,
        gameInfo: GameInfo,
        parameterValues: ParameterValues,
        statValues: HashMap<DofusBotScriptStat, String>,
    ) {
        val explorationParameters = ExplorationParameters(
            killEverything = parameterValues.getParamValue(killEverythingParameter),
            maxMonsterGroupLevel = parameterValues.getParamValue(maxMonsterGroupLevelParameter),
            maxMonsterGroupSize = parameterValues.getParamValue(maxMonsterGroupSizeParameter),
            searchedMonsterName = parameterValues.getParamValue(searchedMonsterParameter),
            stopWhenArchMonsterFound = parameterValues.getParamValue(stopWhenArchMonsterFoundParameter),
            stopWhenWantedMonsterFound = parameterValues.getParamValue(stopWhenQuestMonsterFoundParameter),
            useZaaps = parameterValues.getParamValue(useZaapsParameter),
            explorationThresholdMinutes = getExplorationThresholdMinutes(parameterValues),
        )
        val success = when (parameterValues.getParamValue(explorationTypeParameter)) {
            ExplorationType.SubArea -> exploreSubAreas(logItem, gameInfo, parameterValues, explorationParameters)
            ExplorationType.Path -> explorePath(logItem, gameInfo, parameterValues, explorationParameters)
        }
        if (!success) {
            error("Failed exploration")
        }
    }

    private fun explorePath(
        logItem: LogItem,
        gameInfo: GameInfo,
        parameterValues: ParameterValues,
        explorationParameters: ExplorationParameters,
    ): Boolean {
        val path = parameterValues.getParamValue(pathParameter)
            ?: error("You must select a path.")
        return ExploreSubPathsTask(
            subPaths = path.subPaths.filter { it.enabled },
            runForever = parameterValues.getParamValue(runForeverParameter),
            explorationParameters = explorationParameters,
        ).run(logItem, gameInfo)
    }

    private fun exploreSubAreas(
        logItem: LogItem,
        gameInfo: GameInfo,
        parameterValues: ParameterValues,
        explorationParameters: ExplorationParameters,
    ): Boolean {
        val subAreas = if (parameterValues.getParamValue(currentAreaParameter)) {
            listOf(gameInfo.currentMap.subArea)
        } else parameterValues.getParamValue(subAreasParameter)
        return ExploreSubAreasTask(
            subAreas = subAreas,
            runForever = parameterValues.getParamValue(runForeverParameter),
            explorationParameters = explorationParameters,
        ).run(logItem, gameInfo)
    }

    private fun getExplorationThresholdMinutes(parameterValues: ParameterValues): Int {
        val runForever = parameterValues.getParamValue(runForeverParameter)
        return if (!runForever) {
            parameterValues.getParamValue(ignoreMapsExploredRecentlyParameter)
        } else 0
    }

    enum class ExplorationType(val strValue: String) {
        SubArea("Sub area"),
        Path("Path")
    }
}