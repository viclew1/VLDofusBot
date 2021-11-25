package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.game.move.transporters.ITransporter
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.TravelToCoordinatesTask
import fr.lewon.dofus.bot.scripts.tasks.impl.npc.NpcSpeakTask
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class TransportTowardTask(private val transporter: ITransporter) : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): DofusMap {
        ZaapTowardTask(transporter.getClosestZaap()).run(logItem, gameInfo, cancellationToken)
        TravelToCoordinatesTask(transporter.getTransporterCoordinates()).run(logItem, gameInfo, cancellationToken)
        WaitUtil.sleep(2000)
        NpcSpeakTask(transporter.getNpcPointRelative(), transporter.getOptionPointRelative())
            .run(logItem, gameInfo, cancellationToken)
        val mapInfo = WaitUtil.waitForEvent(
            gameInfo.snifferId,
            MapComplementaryInformationsDataMessage::class.java,
            cancellationToken = cancellationToken
        ).map
        WaitUtil.waitForEvent(
            gameInfo.snifferId,
            BasicNoOperationMessage::class.java,
            cancellationToken = cancellationToken
        )
        return mapInfo
    }

    override fun onStarted(): String {
        val from = transporter.getTransporterCoordinates()
        val to = transporter.getCoordinates()
        return "Using transporter from [${from.x}, ${from.y}] to [${to.x}, ${to.y}] ..."
    }
}