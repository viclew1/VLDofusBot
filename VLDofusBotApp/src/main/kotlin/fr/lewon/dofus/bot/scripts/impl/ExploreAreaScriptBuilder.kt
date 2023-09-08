package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.d2o.managers.map.SubAreaManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.BooleanParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.IntParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.MultiChoiceParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.StringParameter
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.ExploreSubAreaTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.ExploreSubAreasTask
import fr.lewon.dofus.bot.util.StringUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

object ExploreAreaScriptBuilder : DofusBotScriptBuilder("Explore area") {

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

    val currentAreaParameter = BooleanParameter(
        "Run in current area",
        "If checked, ignores sub area parameter and explores current area",
        false,
        parametersGroup = 1
    )

    val subAreasParameter = MultiChoiceParameter(
        "Sub areas",
        "Sub areas to explore",
        listOf(SUB_AREA_BY_LABEL.values.first()),
        getAvailableValues = { SUB_AREA_BY_LABEL.entries.sortedBy { StringUtil.removeAccents(it.key) }.map { it.value } },
        displayCondition = { !it.getParamValue(currentAreaParameter) },
        itemValueToString = { it.label },
        stringToItemValue = { SUB_AREA_BY_LABEL[it] ?: error("Sub area not found : $it") },
        parametersGroup = 1
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
        currentAreaParameter,
        subAreasParameter,
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
        return "Explore all maps of selected sub area"
    }

    override fun doExecuteScript(
        logItem: LogItem,
        gameInfo: GameInfo,
        parameterValues: ParameterValues,
        statValues: HashMap<DofusBotScriptStat, String>,
    ) {
        val subAreas = if (parameterValues.getParamValue(currentAreaParameter)) {
            listOf(gameInfo.currentMap.subArea)
        } else parameterValues.getParamValue(subAreasParameter)
        val runForever = parameterValues.getParamValue(runForeverParameter)
        val explorationThresholdMinutes = if (!runForever) {
            parameterValues.getParamValue(ignoreMapsExploredRecentlyParameter)
        } else 0
        ExploreSubAreasTask(
            subAreas = subAreas,
            killEverything = parameterValues.getParamValue(killEverythingParameter),
            maxMonsterGroupLevel = parameterValues.getParamValue(maxMonsterGroupLevelParameter),
            maxMonsterGroupSize = parameterValues.getParamValue(maxMonsterGroupSizeParameter),
            searchedMonsterName = parameterValues.getParamValue(searchedMonsterParameter),
            stopWhenArchMonsterFound = parameterValues.getParamValue(stopWhenArchMonsterFoundParameter),
            stopWhenWantedMonsterFound = parameterValues.getParamValue(stopWhenQuestMonsterFoundParameter),
            runForever = runForever,
            explorationThresholdMinutes = explorationThresholdMinutes,
            useZaaps = parameterValues.getParamValue(useZaapsParameter)
        ).run(logItem, gameInfo)
    }

}