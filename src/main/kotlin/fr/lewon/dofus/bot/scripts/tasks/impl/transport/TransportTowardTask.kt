package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.game.move.transporters.ITransporter
import fr.lewon.dofus.bot.game.move.transporters.TravelUtil
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.TravelTask
import fr.lewon.dofus.bot.scripts.tasks.impl.npc.NpcSpeakTask
import fr.lewon.dofus.bot.util.game.GeneralUIGameUtil
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class TransportTowardTask(private val transporter: ITransporter) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val closestZaap = TravelUtil.getClosestZaap(listOf(transporter.getMap()))
            ?: error("No zaap found near transporter")
        ZaapTowardTask(closestZaap.first).run(logItem, gameInfo)
        TravelTask(listOf(transporter.getTransporterMap())).run(logItem, gameInfo)
        WaitUtil.sleep(2000)
        NpcSpeakTask(transporter.getNpcPointRelative(), transporter.getOptionPointRelative())
            .run(logItem, gameInfo)
        MoveUtil.waitForMapChange(gameInfo)
        return WaitUtil.waitUntil({ GeneralUIGameUtil.isGameReadyToUse(gameInfo) })
    }

    override fun onStarted(): String {
        val from = transporter.getTransporterMap().getCoordinates()
        val to = transporter.getMap().getCoordinates()
        return "Using transporter from [${from.x}, ${from.y}] to [${to.x}, ${to.y}] ..."
    }
}