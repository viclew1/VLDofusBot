package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.d2o.managers.map.SubAreaManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.parameters.MultipleParameterValuesSeparator
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.ExploreSubAreaTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.ExploreSubAreasTask
import fr.lewon.dofus.bot.util.StringUtil
import fr.lewon.dofus.bot.util.filemanagers.impl.HarvestableSetsManager
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
    private val SUB_AREA_LABELS = SUB_AREA_BY_LABEL.keys.sortedBy { StringUtil.removeAccents(it) }

    val currentAreaParameter = DofusBotParameter(
        "Run in current area",
        "If checked, ignores sub area parameter and explores current area",
        "false",
        DofusBotParameterType.BOOLEAN,
        parametersGroup = 1
    )

    val subAreasParameter = DofusBotParameter(
        "Sub areas",
        "Sub areas to explore",
        SUB_AREA_LABELS.firstOrNull() ?: "",
        DofusBotParameterType.MULTIPLE_CHOICE,
        SUB_AREA_LABELS,
        displayCondition = { it.getParamValue(currentAreaParameter) == "false" },
        parametersGroup = 1
    )

    val runForeverParameter = DofusBotParameter(
        "Run forever",
        "Explores this area until you manually stop",
        "false",
        DofusBotParameterType.BOOLEAN,
        parametersGroup = 2
    )

    val killEverythingParameter = DofusBotParameter(
        "Kill everything",
        "Fights every group of monsters present on the maps",
        "false",
        DofusBotParameterType.BOOLEAN,
        parametersGroup = 2
    )

    val searchedMonsterParameter = DofusBotParameter(
        "Searched monster",
        "Monster which will stop exploration when found. Leave empty if you're not seeking a monster",
        "",
        DofusBotParameterType.STRING,
        parametersGroup = 3
    )

    val stopWhenArchMonsterFoundParameter = DofusBotParameter(
        "Stop when arch monster found",
        "Stops exploration when you find an arch monster",
        "true",
        DofusBotParameterType.BOOLEAN,
        parametersGroup = 3
    )

    val stopWhenQuestMonsterFoundParameter = DofusBotParameter(
        "Stop when quest monster found",
        "Stops exploration when you find a quest monster",
        "false",
        DofusBotParameterType.BOOLEAN,
        parametersGroup = 3
    )

    val ignoreMapsExploredRecentlyParameter = DofusBotParameter(
        "Ignore maps you explored in the last X min. (0 to explore all)",
        "Ignore maps any of your character explored less than the passed value (in minutes). Set to 0 or less to ignore.",
        "15",
        DofusBotParameterType.INTEGER,
        parametersGroup = 4
    )

    val harvestParameter = DofusBotParameter(
        "Harvestable set",
        "Harvest any resource in this set",
        HarvestableSetsManager.defaultHarvestableIdsBySetName.keys.first(),
        DofusBotParameterType.CHOICE,
        possibleValues = HarvestableSetsManager.getHarvestableIdsBySetName().keys.toList(),
        parametersGroup = 5
    )

    override fun getParameters(): List<DofusBotParameter> = listOf(
        currentAreaParameter,
        subAreasParameter,
        stopWhenArchMonsterFoundParameter,
        stopWhenQuestMonsterFoundParameter,
        searchedMonsterParameter,
        runForeverParameter,
        killEverythingParameter,
        ignoreMapsExploredRecentlyParameter,
        harvestParameter,
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
        scriptValues: ScriptValues,
        statValues: HashMap<DofusBotScriptStat, String>
    ) {
        val currentAreaParameterValue = scriptValues.getParamValue(currentAreaParameter).toBoolean()
        val subAreas = if (currentAreaParameterValue) {
            listOf(gameInfo.currentMap.subArea)
        } else {
            val subAreaParameterValue = scriptValues.getParamValue(subAreasParameter)
            val labels = subAreaParameterValue.split(MultipleParameterValuesSeparator)
            labels.map { SUB_AREA_BY_LABEL[it] ?: error("Sub area not found : $it") }
        }
        val harvestableSetName = scriptValues.getParamValue(harvestParameter)
        val itemIdsToHarvest = HarvestableSetsManager.getItemsToHarvest(harvestableSetName)
        ExploreSubAreasTask(
            subAreas = subAreas,
            killEverything = scriptValues.getParamValue(killEverythingParameter).toBoolean(),
            searchedMonsterName = scriptValues.getParamValue(searchedMonsterParameter),
            stopWhenArchMonsterFound = scriptValues.getParamValue(stopWhenArchMonsterFoundParameter).toBoolean(),
            stopWhenWantedMonsterFound = scriptValues.getParamValue(stopWhenQuestMonsterFoundParameter).toBoolean(),
            runForever = scriptValues.getParamValue(runForeverParameter).toBoolean(),
            explorationThresholdMinutes = scriptValues.getParamValue(ignoreMapsExploredRecentlyParameter).toInt(),
            itemIdsToHarvest = itemIdsToHarvest
        ).run(logItem, gameInfo)
    }

}