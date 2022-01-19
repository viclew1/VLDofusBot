package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.d2o.managers.MapManager
import fr.lewon.dofus.bot.core.manager.d2o.managers.SubAreaManager
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.ExploreSubAreaTask
import fr.lewon.dofus.bot.util.network.GameInfo

class ExploreAreaScript : DofusBotScript("Explore area") {

    companion object {
        private val SUB_AREAS = SubAreaManager.getAllSubAreas()
            .filter { MapManager.getDofusMaps(it).isNotEmpty() }
        private val SUB_AREA_BY_LABEL = SUB_AREAS.associateBy { "${it.area.name} (${it.name})" }
        private val SUB_AREA_LABELS = SUB_AREA_BY_LABEL.keys.sorted()

        private fun getSubAreaWorldMap(subArea: DofusSubArea): Int {
            return MapManager.getDofusMaps(subArea).groupBy { it.worldMap }
                .maxByOrNull { it.value.size }
                ?.key
                ?: 1
        }
    }

    private val subAreaParameter = DofusBotParameter(
        "sub_area", "Dofus sub area", "", DofusBotParameterType.CHOICE, SUB_AREA_LABELS
    )


    override fun getParameters(): List<DofusBotParameter> {
        return listOf(subAreaParameter)
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Explore all maps of selected sub area"
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val subAreaParameterValue = subAreaParameter.value
        val subArea = SUB_AREA_BY_LABEL[subAreaParameterValue] ?: error("Sub area not found : $subAreaParameterValue")
        ExploreSubAreaTask(subArea, getSubAreaWorldMap(subArea)).run(logItem, gameInfo)
    }

}