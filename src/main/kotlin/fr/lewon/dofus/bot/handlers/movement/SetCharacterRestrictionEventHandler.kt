package fr.lewon.dofus.bot.handlers.movement

import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIState
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.move.SetCharacterRestrictionsMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object SetCharacterRestrictionEventHandler : IEventHandler<SetCharacterRestrictionsMessage> {
    override fun onEventReceived(socketResult: SetCharacterRestrictionsMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        if (gameInfo.initRequested) {
            gameInfo.playerId = socketResult.actorId
            gameInfo.updateCellData(gameInfo.currentMap.id)
            CharactersUIState.updateState(gameInfo.character)
            println("${gameInfo.character.pseudo} initialized, ID : ${gameInfo.playerId}")
            gameInfo.shouldInitBoard = false
            gameInfo.initRequested = false
        }
    }
}