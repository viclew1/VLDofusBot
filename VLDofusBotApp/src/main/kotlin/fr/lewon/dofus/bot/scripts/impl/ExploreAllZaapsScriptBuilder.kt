package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.TravelTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.LeaveHavenBagTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.OpenZaapInterfaceTask
import fr.lewon.dofus.bot.util.game.TravelUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import fr.lewon.dofus.bot.util.ui.UiUtil

object ExploreAllZaapsScriptBuilder : DofusBotScriptBuilder("Explore all zaaps") {

    override fun getParameters(): List<DofusBotParameter> {
        return emptyList()
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf(exploredStat)
    }

    override fun getDescription(): String {
        return "Explores all accessible zaap maps"
    }

    private val exploredStat = DofusBotScriptStat("explored", "/")

    override fun doExecuteScript(logItem: LogItem, gameInfo: GameInfo, scriptValues: ScriptValues) {
        val registeredZaaps = OpenZaapInterfaceTask().run(logItem, gameInfo)
        val closeZaapInterfaceButtonBounds = UiUtil.getContainerBounds(DofusUIElement.ZAAP_SELECTION, "btn_close")
        MouseUtil.leftClick(gameInfo, closeZaapInterfaceButtonBounds.getCenter())
        LeaveHavenBagTask().run(logItem, gameInfo)
        val zaaps = TravelUtil.getAllZaapMaps()
            .filter { shouldExploreZaap(gameInfo, it, registeredZaaps) }
            .sortedBy { it.coordinates.distanceTo(gameInfo.currentMap.coordinates) }
            .toMutableList()
        val totalSize = zaaps.size
        exploredStat.value = "0 / $totalSize"
        while (zaaps.isNotEmpty() && TravelTask(zaaps).run(logItem, gameInfo)) {
            zaaps.remove(gameInfo.currentMap)
            exploredStat.value = "${totalSize - zaaps.size} / $totalSize"
        }
        if (zaaps.isNotEmpty()) {
            val destMapsStr = zaaps.joinToString(", ") { "(${it.posX}; ${it.posY})" }
            error("Couldn't explore all zaaps : $destMapsStr")
        }
    }

    private fun shouldExploreZaap(gameInfo: GameInfo, it: DofusMap, registeredZaaps: List<DofusMap>): Boolean {
        return !registeredZaaps.contains(it)
                && it.subArea.area.superAreaId == gameInfo.currentMap.subArea.area.superAreaId
                && TravelUtil.getPath(gameInfo, it) != null
                && TravelUtil.getReversePath(gameInfo, it) != null
    }
}