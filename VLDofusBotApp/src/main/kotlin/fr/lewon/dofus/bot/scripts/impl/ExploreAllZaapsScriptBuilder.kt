package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MoveTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.LeaveHavenBagTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.OpenZaapInterfaceTask
import fr.lewon.dofus.bot.util.game.TravelUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import fr.lewon.dofus.bot.util.ui.UiUtil

object ExploreAllZaapsScriptBuilder : DofusBotScriptBuilder("Explore all zaaps") {

    private val nextZaapStat = DofusBotScriptStat("Next zaap")
    private val exploredStat = DofusBotScriptStat("Explored")

    override fun getParameters(): List<DofusBotParameter> {
        return emptyList()
    }

    override fun getDefaultStats(): List<DofusBotScriptStat> {
        return listOf(exploredStat, nextZaapStat)
    }

    override fun getDescription(): String {
        return "Explores all accessible zaap maps"
    }

    override fun doExecuteScript(
        logItem: LogItem,
        gameInfo: GameInfo,
        scriptValues: ScriptValues,
        statValues: HashMap<DofusBotScriptStat, String>
    ) {
        val registeredZaaps = OpenZaapInterfaceTask().run(logItem, gameInfo)
        val nonExploredZaaps = TravelUtil.getAllZaapMaps().minus(registeredZaaps.toSet())
        val closeZaapInterfaceButtonBounds = UiUtil.getContainerBounds(DofusUIElement.ZAAP_SELECTION, "btn_close")
        MouseUtil.leftClick(gameInfo, closeZaapInterfaceButtonBounds.getCenter())
        LeaveHavenBagTask().run(logItem, gameInfo)
        val toExploreZaaps = TravelUtil.getAllZaapMaps().filter { shouldExploreZaap(gameInfo, it, nonExploredZaaps) }
            .toMutableList()
        val totalSize = toExploreZaaps.size
        statValues[exploredStat] = "0 / $totalSize"
        while (toExploreZaaps.isNotEmpty()) {
            val characterInfo = gameInfo.buildCharacterBasicInfo(toExploreZaaps.map { it.id })
            val path = TravelUtil.getPath(gameInfo, toExploreZaaps, characterInfo)
                ?: error("Couldn't find a path to destination")
            val destMap = path.lastOrNull()?.edge?.to?.mapId?.let { MapManager.getDofusMap(it) }
                ?: error("No transition in path")
            val nextMapStr = "(${destMap.posX}; ${destMap.posY})"
            statValues[nextZaapStat] = nextMapStr
            val subLogItem = gameInfo.logger.addSubLog("Reaching zaap : $nextMapStr", logItem)
            if (!MoveTask(path).run(subLogItem, gameInfo)) {
                gameInfo.logger.closeLog("KO", subLogItem)
                val destMapsStr = toExploreZaaps.joinToString(", ") { "(${it.posX}; ${it.posY})" }
                error("Couldn't explore all zaaps : $destMapsStr")
            }
            gameInfo.logger.closeLog("OK", subLogItem)
            toExploreZaaps.remove(gameInfo.currentMap)
            statValues[exploredStat] = "${totalSize - toExploreZaaps.size} / $totalSize"
        }
    }

    private fun shouldExploreZaap(gameInfo: GameInfo, it: DofusMap, nonExploredZaaps: List<DofusMap>): Boolean {
        val characterInfo = gameInfo.buildCharacterBasicInfo(nonExploredZaaps.map { it.id })
        return nonExploredZaaps.contains(it)
                && it.subArea.area.superAreaId == gameInfo.currentMap.subArea.area.superAreaId
                && TravelUtil.getPath(gameInfo, it, characterInfo) != null
                && TravelUtil.getReversePath(gameInfo, it, characterInfo) != null
    }
}