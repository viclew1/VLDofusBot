package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.map.WaypointManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.game.move.transporters.TravelUtil
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.TravelTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.LeaveHavenBagTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.OpenZaapInterfaceTask
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import java.awt.event.KeyEvent

class ExploreAllZaapsScript : DofusBotScript("Explore all zaaps") {

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

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val registeredZaaps = OpenZaapInterfaceTask().run(logItem, gameInfo)
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))
        KeyboardUtil.sendKey(gameInfo, KeyEvent.VK_ESCAPE)
        LeaveHavenBagTask().run(logItem, gameInfo)
        val zaaps = WaypointManager.getAllZaapMaps()
            .filter { shouldExploreZaap(gameInfo, it, registeredZaaps) }
            .sortedBy { it.getCoordinates().distanceTo(gameInfo.currentMap.getCoordinates()) }
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