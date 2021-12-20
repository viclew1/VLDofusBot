package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt.TreasureHuntMessage
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFight
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToHint
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object TreasureHuntEventHandler : EventHandler<TreasureHuntMessage> {

    override fun onEventReceived(socketResult: TreasureHuntMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.treasureHunt = socketResult
        gameInfo.logger.debug("Treasure hunt start map : [${socketResult.startMap.posX},${socketResult.startMap.posY}]")
        socketResult.huntSteps.lastOrNull()?.let {
            when (it) {
                is TreasureHuntStepFollowDirectionToPOI -> gameInfo.logger.debug("Current step : POI ; direction : ${it.direction} ; label : ${it.label}")
                is TreasureHuntStepFollowDirectionToHint -> gameInfo.logger.debug("Current step : HINT ; direction : ${it.direction} ; npcId : ${it.npcId}")
                is TreasureHuntStepFight -> gameInfo.logger.debug("Current step : FIGHT")
                else -> error("Unknown hunt step type")
            }
        }
    }

}