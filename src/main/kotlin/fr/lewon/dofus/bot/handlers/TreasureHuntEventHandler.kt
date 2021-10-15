package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt.TreasureHuntMessage
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFight
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToHint
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object TreasureHuntEventHandler : EventHandler<TreasureHuntMessage> {

    override fun onEventReceived(socketResult: TreasureHuntMessage) {
        GameInfo.treasureHunt = socketResult
        VldbLogger.debug("Treasure hunt start map : [${socketResult.startMap.posX},${socketResult.startMap.posY}]")
        socketResult.huntSteps.lastOrNull()?.let {
            when (it) {
                is TreasureHuntStepFollowDirectionToPOI -> VldbLogger.debug("Current step : POI ; direction : ${it.direction} ; label : ${it.label}")
                is TreasureHuntStepFollowDirectionToHint -> VldbLogger.debug("Current step : HINT ; direction : ${it.direction} ; npcId : ${it.npcId}")
                is TreasureHuntStepFight -> VldbLogger.debug("Current step : FIGHT")
                else -> error("Unknown hunt step type")
            }
        }
    }

}