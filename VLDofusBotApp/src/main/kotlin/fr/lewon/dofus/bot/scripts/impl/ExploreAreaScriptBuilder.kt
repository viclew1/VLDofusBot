package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.d2o.managers.map.SubAreaManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.model.jobs.Jobs
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.ExploreSubAreaTask
import fr.lewon.dofus.bot.util.StringUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

object ExploreAreaScriptBuilder : DofusBotScriptBuilder("Explore area") {

    private val SUB_AREAS = SubAreaManager.getAllSubAreas()
        .filter {
            ExploreSubAreaTask.SUB_AREA_ID_FULLY_ALLOWED.contains(it.id)
                    || MapManager.getDofusMaps(it).isNotEmpty()
                    && !it.isConquestVillage
                    && it.monsters.isNotEmpty()
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

    val subAreaParameter = DofusBotParameter(
        "Sub area",
        "Dofus sub area",
        SUB_AREA_LABELS.firstOrNull() ?: "",
        DofusBotParameterType.CHOICE,
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
        "Harvest resources",
        "Harvest any resource found",
        "false",
        DofusBotParameterType.BOOLEAN,
        parametersGroup = 5
    )

    val harvestJobParameterByJob = Jobs.values().associateWith { job ->
        DofusBotParameter(
            "Harvest [${job.name}] resources",
            "",
            "true",
            DofusBotParameterType.BOOLEAN,
            Jobs.values().map { it.name },
            displayCondition = { it.getParamValue(harvestParameter) == "true" },
            parametersGroup = 5
        )
    }

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(
            currentAreaParameter,
            subAreaParameter,
            stopWhenArchMonsterFoundParameter,
            stopWhenQuestMonsterFoundParameter,
            searchedMonsterParameter,
            runForeverParameter,
            killEverythingParameter,
            ignoreMapsExploredRecentlyParameter,
            harvestParameter,
        ).plus(harvestJobParameterByJob.values)
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return emptyList()
    }

    override fun getDescription(): String {
        return "Explore all maps of selected sub area"
    }

    override fun doExecuteScript(logItem: LogItem, gameInfo: GameInfo, scriptValues: ScriptValues) {
        val currentAreaParameterValue = scriptValues.getParamValue(currentAreaParameter).toBoolean()
        val subArea = if (currentAreaParameterValue) {
            gameInfo.currentMap.subArea
        } else {
            val subAreaParameterValue = scriptValues.getParamValue(subAreaParameter)
            SUB_AREA_BY_LABEL[subAreaParameterValue] ?: error("Sub area not found : $subAreaParameterValue")
        }
        val jobsToHarvest = if (scriptValues.getParamValue(harvestParameter).toBoolean()) {
            Jobs.values().filter { shouldHarvestJob(it, scriptValues) }
        } else emptyList()
        ExploreSubAreaTask(
            subArea,
            killEverything = scriptValues.getParamValue(killEverythingParameter).toBoolean(),
            searchedMonsterName = scriptValues.getParamValue(searchedMonsterParameter),
            stopWhenArchMonsterFound = scriptValues.getParamValue(stopWhenArchMonsterFoundParameter).toBoolean(),
            stopWhenWantedMonsterFound = scriptValues.getParamValue(stopWhenQuestMonsterFoundParameter).toBoolean(),
            runForever = scriptValues.getParamValue(runForeverParameter).toBoolean(),
            explorationThresholdMinutes = scriptValues.getParamValue(ignoreMapsExploredRecentlyParameter).toInt(),
            jobsToHarvest = jobsToHarvest
        ).run(logItem, gameInfo)
    }

    private fun shouldHarvestJob(job: Jobs, scriptValues: ScriptValues): Boolean {
        val jobParameter = harvestJobParameterByJob[job] ?: return false
        return scriptValues.getParamValue(jobParameter).toBoolean()
    }

}