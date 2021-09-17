package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightDeathMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.ui.ConsoleLogger

object GameActionFightDeathEventHandler : EventHandler<GameActionFightDeathMessage> {

    override fun onEventReceived(socketResult: GameActionFightDeathMessage) {
        GameInfo.fightBoard.killFighter(socketResult.targetId)
        ConsoleLogger.info("Fighter [${socketResult.targetId}] has been killed by fighter [${socketResult.sourceId}]")
    }

}