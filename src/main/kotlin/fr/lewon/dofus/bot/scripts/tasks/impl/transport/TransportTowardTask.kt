package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.game.move.transporters.ITransporter
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.npc.NpcSpeakTask
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.logs.LogItem

class TransportTowardTask(private val transporter: ITransporter) : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        ZaapTowardTask(transporter.getClosestZaap()).run(logItem)
        TravelTask(transporter.getTransporterCoordinates()).run(logItem)
        NpcSpeakTask(transporter.getNpcPointRelative(), transporter.getOptionPointRelative()).run(logItem)
        val mapInfo = WaitUtil.waitForEvent(MapComplementaryInformationsDataMessage::class.java).map
        WaitUtil.waitForEvent(BasicNoOperationMessage::class.java)
        return mapInfo
    }

    override fun onStarted(): String {
        val from = transporter.getTransporterCoordinates()
        val to = transporter.getCoordinates()
        return "Using transporter from [${from.x}, ${from.y}] to [${to.x}, ${to.y}] ..."
    }
}