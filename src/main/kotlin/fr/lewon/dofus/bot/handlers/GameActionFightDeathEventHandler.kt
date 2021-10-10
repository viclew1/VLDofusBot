package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightDeathMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.logs.VldbLogger

object GameActionFightDeathEventHandler : EventHandler<GameActionFightDeathMessage> {

    override fun onEventReceived(socketResult: GameActionFightDeathMessage) {
        GameInfo.fightBoard.killFighter(socketResult.targetId)
        VldbLogger.info("Fighter [${socketResult.targetId}] has been killed by fighter [${socketResult.sourceId}]")
    }

}