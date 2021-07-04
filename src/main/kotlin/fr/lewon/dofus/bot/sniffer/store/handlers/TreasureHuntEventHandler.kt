package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.TreasureHuntMessage
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToHint
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object TreasureHuntEventHandler : EventHandler<TreasureHuntMessage> {

    override fun onEventReceived(socketResult: TreasureHuntMessage) {
        println("quest type : ${socketResult.questType}")
        println("start map : [${socketResult.startMap.posX},${socketResult.startMap.posY}]")
        val huntSteps = socketResult.huntSteps
        println("Known steps length : ${huntSteps.size}")
        for (step in huntSteps) {
            if (step is TreasureHuntStepFollowDirectionToPOI) {
                val direction = step.direction
                val label = step.label
                println(" - POI : direction : $direction ; label : $label")
            } else if (step is TreasureHuntStepFollowDirectionToHint) {
                val direction = step.direction
                val npcId = step.npcId
                println(" - HINT : direction : $direction ; npcId : $npcId")
            }
        }
        println("Total step count : ${socketResult.totalStepCount}")
        println("Check point current : ${socketResult.checkPointCurrent}")
        println("Check point total : ${socketResult.checkPointTotal}")
        println("Available retry count : ${socketResult.availableRetryCount}")
        GameInfo.treasureHunt = socketResult
    }

}