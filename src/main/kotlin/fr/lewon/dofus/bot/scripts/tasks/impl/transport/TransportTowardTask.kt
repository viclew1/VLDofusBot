package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.game.move.transporters.ITransporter
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.npc.NpcSpeakTask
import fr.lewon.dofus.bot.util.game.GeneralUIGameUtil
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class TransportTowardTask(private val transporter: ITransporter) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        ReachMapTask(listOf(transporter.getTransporterMap())).run(logItem, gameInfo)
        WaitUtil.sleep(2000)
        NpcSpeakTask(transporter.getNpcId(), listOf(transporter.getOptionIndex())).run(logItem, gameInfo)
        MoveUtil.waitForMapChange(gameInfo)
        return WaitUtil.waitUntil({ GeneralUIGameUtil.isGameReadyToUse(gameInfo) })
    }

    override fun onStarted(): String {
        val from = transporter.getTransporterMap().getCoordinates()
        val to = transporter.getMap().getCoordinates()
        return "Using transporter from [${from.x}, ${from.y}] to [${to.x}, ${to.y}] ..."
    }
}