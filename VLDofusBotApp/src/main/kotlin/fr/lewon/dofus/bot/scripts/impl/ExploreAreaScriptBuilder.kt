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
        DofusBotParameterType.BOOLEAN
    )

    val subAreaParameter = DofusBotParameter(
        "Sub area",
        "Dofus sub area",
        SUB_AREA_LABELS.firstOrNull() ?: "",
        DofusBotParameterType.CHOICE,
        SUB_AREA_LABELS,
        displayCondition = { it.getParamValue(currentAreaParameter) == "false" }
    )

    val runForeverParameter = DofusBotParameter(
        "Run forever",
        "Explores this area until you manually stop",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    val killEverythingParameter = DofusBotParameter(
        "Kill everything",
        "Fights every group of monsters present on the maps",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    val searchedMonsterParameter = DofusBotParameter(
        "Searched monster",
        "Monster which will stop exploration when found. Leave empty if you're not seeking a monster",
        "",
        DofusBotParameterType.STRING
    )

    val stopWhenArchMonsterFoundParameter = DofusBotParameter(
        "Stop when arch monster found",
        "Stops exploration when you find an arch monster",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    val stopWhenQuestMonsterFoundParameter = DofusBotParameter(
        "Stop when quest monster found",
        "Stops exploration when you find a quest monster",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    val harvestParameter = DofusBotParameter(
        "Harvest resources",
        "Harvest any resource found",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    val harvestAllParameter = DofusBotParameter(
        "All harvest tasks",
        "         ",
        "false",
        DofusBotParameterType.BOOLEAN,
        displayCondition = { it.getParamValue(harvestParameter) == "true" }
    )

    val harvestJobParameter = DofusBotParameter(
        "Harvest task",
        "         ",
        "Chop",
        DofusBotParameterType.CHOICE,
        listOf("Chop", "Collect", "Fish", "Gather", "Mow"),
        displayCondition = { it.getParamValue(harvestAllParameter) == "false" && it.getParamValue(harvestParameter) == "true"}
    )


    override fun getParameters(): List<DofusBotParameter> {
        return listOf(
            currentAreaParameter,
            subAreaParameter,
            stopWhenArchMonsterFoundParameter,
            stopWhenQuestMonsterFoundParameter,
            searchedMonsterParameter,
            runForeverParameter,
            killEverythingParameter,
            harvestParameter,
            harvestAllParameter,
            harvestJobParameter,
        )
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return emptyList()
    }

    override fun getDescription(): String {
        return "Explore all maps of selected sub area"
    }

    override fun doExecuteScript(logItem: LogItem, gameInfo: GameInfo, scriptValues: ScriptValues) {
        val currentAreaParameterValue = scriptValues.getParamValue(currentAreaParameter).toBoolean()
        val harvestAllParameterValue = scriptValues.getParamValue(harvestAllParameter).toBoolean()
        val subArea = if (currentAreaParameterValue) {
            gameInfo.currentMap.subArea
        } else {
            val subAreaParameterValue = scriptValues.getParamValue(subAreaParameter)
            SUB_AREA_BY_LABEL[subAreaParameterValue] ?: error("Sub area not found : $subAreaParameterValue")
        }
        ExploreSubAreaTask(
            subArea,
            killEverything = scriptValues.getParamValue(killEverythingParameter).toBoolean(),
            searchedMonsterName = scriptValues.getParamValue(searchedMonsterParameter),
            stopWhenArchMonsterFound = scriptValues.getParamValue(stopWhenArchMonsterFoundParameter).toBoolean(),
            stopWhenWantedMonsterFound = scriptValues.getParamValue(stopWhenQuestMonsterFoundParameter).toBoolean(),
            runForever = scriptValues.getParamValue(runForeverParameter).toBoolean(),
            harvestResource = scriptValues.getParamValue(harvestParameter).toBoolean(),
            harvestJob = if (harvestAllParameterValue) {
                ""
            } else {
                scriptValues.getParamValue(harvestJobParameter)
            }

        ).run(logItem, gameInfo)
    }

}