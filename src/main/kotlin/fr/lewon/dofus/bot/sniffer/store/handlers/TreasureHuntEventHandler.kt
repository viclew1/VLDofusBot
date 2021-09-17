package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt.TreasureHuntMessage
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFight
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToHint
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.ui.ConsoleLogger

object TreasureHuntEventHandler : EventHandler<TreasureHuntMessage> {

    override fun onEventReceived(socketResult: TreasureHuntMessage) {
        GameInfo.treasureHunt = socketResult
        ConsoleLogger.info("Treasure hunt start map : [${socketResult.startMap.posX},${socketResult.startMap.posY}]")
        socketResult.huntSteps.lastOrNull()?.let {
            when (it) {
                is TreasureHuntStepFollowDirectionToPOI -> {
                    val direction = it.direction
                    val label = it.label
                    ConsoleLogger.info("Current step : POI ; direction : $direction ; label : $label")
                }
                is TreasureHuntStepFollowDirectionToHint -> {
                    val direction = it.direction
                    val npcId = it.npcId
                    ConsoleLogger.info("Current step : HINT ; direction : $direction ; npcId : $npcId")
                }
                is TreasureHuntStepFight -> {
                    ConsoleLogger.info("Current step : FIGHT")
                }
            }
        }
    }

}