package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil

class CustomMoveTask(private val location: PointRelative) : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        MouseUtil.leftClick(location, false, 0)
        return EventStore.waitForEvent(MapComplementaryInformationsDataMessage::class.java).dofusMap
    }

    override fun onStarted(): String {
        return "Moving to point [${location.x}, ${location.y}]"
    }
}