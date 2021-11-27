package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.game.move.transporters.ITransporter
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.TravelToCoordinatesTask
import fr.lewon.dofus.bot.scripts.tasks.impl.npc.NpcSpeakTask
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class TransportTowardTask(private val transporter: ITransporter) : BooleanDofusBotTask() {

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        ZaapTowardTask(transporter.getClosestZaap()).run(logItem, gameInfo, cancellationToken)
        TravelToCoordinatesTask(transporter.getTransporterCoordinates()).run(logItem, gameInfo, cancellationToken)
        WaitUtil.sleep(2000)
        NpcSpeakTask(transporter.getNpcPointRelative(), transporter.getOptionPointRelative())
            .run(logItem, gameInfo, cancellationToken)
        return MoveUtil.waitForMapChange(gameInfo, cancellationToken)
    }

    override fun onStarted(): String {
        val from = transporter.getTransporterCoordinates()
        val to = transporter.getCoordinates()
        return "Using transporter from [${from.x}, ${from.y}] to [${to.x}, ${to.y}] ..."
    }
}