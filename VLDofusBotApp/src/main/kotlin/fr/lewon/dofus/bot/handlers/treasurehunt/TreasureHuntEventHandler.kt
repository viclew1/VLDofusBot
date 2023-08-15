package fr.lewon.dofus.bot.handlers.treasurehunt

import fr.lewon.dofus.bot.core.d2o.managers.hunt.PointOfInterestManager
import fr.lewon.dofus.bot.gui.main.characters.CharactersUIUtil
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.treasureHunt.TreasureHuntMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.treasureHunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object TreasureHuntEventHandler : IEventHandler<TreasureHuntMessage> {

    override fun onEventReceived(socketResult: TreasureHuntMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.treasureHunt = socketResult
        val currentHintName = socketResult.knownStepsList.lastOrNull()?.let { currentHint ->
            if (currentHint is TreasureHuntStepFollowDirectionToPOI) {
                PointOfInterestManager.getPointOfInterest(currentHint.poiLabelId)?.label
            } else null
        }
        CharactersUIUtil.updateHint(gameInfo.character, currentHintName)
    }

}