package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.d2o.managers.map.SubAreaManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.ExploreSubAreaTask
import fr.lewon.dofus.bot.util.network.GameInfo
import java.text.Normalizer

class ExploreAreaScript : DofusBotScript("Explore area") {

    companion object {

        private val SUB_AREAS = SubAreaManager.getAllSubAreas()
            .filter {
                MapManager.getDofusMaps(it).isNotEmpty()
                        && !it.isConquestVillage
                        && it.monsters.isNotEmpty()
                        && it.area.superAreaId == 0
                        && hasNoBoss(it)
            }

        private fun hasNoBoss(subArea: DofusSubArea): Boolean {
            return subArea.monsters.none { it.isBoss }
        }

        private val SUB_AREA_BY_LABEL = SUB_AREAS.associateBy { "${it.area.name} (${it.name})" }
        private val SUB_AREA_LABELS = SUB_AREA_BY_LABEL.keys.sortedBy { removeAccents(it) }

        private fun removeAccents(str: String): String {
            val temp = Normalizer.normalize(str.lowercase(), Normalizer.Form.NFD)
            val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
            return regex.replace(temp, "")
        }
    }

    private val currentAreaParameter = DofusBotParameter(
        "Run in current area",
        "If checked, ignores sub area parameter and explores current area",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    private val subAreaParameter = DofusBotParameter(
        "Sub area", "Dofus sub area", "", DofusBotParameterType.CHOICE, SUB_AREA_LABELS
    )

    private val runForeverParameter = DofusBotParameter(
        "Run forever",
        "Explores this area until you manually stop",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    private val killEverythingParameter = DofusBotParameter(
        "Kill everything",
        "Fights every group of monsters present on the maps",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    private val searchedMonsterParameter = DofusBotParameter(
        "Searched monster",
        "Monster which will stop exploration when found. Leave empty if you're not seeking a monster",
        "",
        DofusBotParameterType.STRING
    )

    private val stopWhenArchMonsterFoundParameter = DofusBotParameter(
        "Stop when arch monster found",
        "Stops exploration when you find an arch monster",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    private val stopWhenWantedMonsterFoundParameter = DofusBotParameter(
        "Stop when wanted monster found",
        "Stops exploration when you find a wanted monster",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(
            currentAreaParameter,
            subAreaParameter,
            stopWhenArchMonsterFoundParameter,
            stopWhenWantedMonsterFoundParameter,
            searchedMonsterParameter,
            runForeverParameter,
            killEverythingParameter,
        )
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Explore all maps of selected sub area"
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val currentAreaParameterValue = currentAreaParameter.value.toBoolean()
        val subArea = if (currentAreaParameterValue) {
            gameInfo.currentMap.subArea
        } else {
            val subAreaParameterValue = subAreaParameter.value
            SUB_AREA_BY_LABEL[subAreaParameterValue] ?: error("Sub area not found : $subAreaParameterValue")
        }
        val runForever = runForeverParameter.value.toBoolean()
        val killEverything = killEverythingParameter.value.toBoolean()
        val searchedMonsterName = searchedMonsterParameter.value
        val stopWhenArchMonsterFound = stopWhenArchMonsterFoundParameter.value.toBoolean()
        val stopWhenWantedMonsterFound = stopWhenWantedMonsterFoundParameter.value.toBoolean()
        ExploreSubAreaTask(
            subArea,
            killEverything,
            searchedMonsterName,
            stopWhenArchMonsterFound,
            stopWhenWantedMonsterFound,
            runForever
        ).run(logItem, gameInfo)
    }

}