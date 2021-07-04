package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.game.move.transporters.Zaap
import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask

open class ReachMapTask(private val dofusMap: DofusMap) : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        if (dofusMap == GameInfo.currentMap) {
            return dofusMap
        }
        val zaaps = Zaap.values().filter { it.altWorld == dofusMap.isAltWorld() }
        val zaap = zaaps.minBy { it.coordinate.distanceTo(dofusMap.getCoordinate()) }
            ?: error("No zaap found")
        ZaapTowardTask(zaap).run(logItem)
        return TravelTask(dofusMap.getCoordinate()).run(logItem)
    }

    override fun onStarted(): String {
        return "Reaching hunt start ..."
    }
}