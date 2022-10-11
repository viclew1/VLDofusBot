package fr.lewon.dofus.bot.handlers.skin

import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.skin.GameContextRefreshEntityLookMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameContextRefreshEntityLookEventHandler : IEventHandler<GameContextRefreshEntityLookMessage> {

    override fun onEventReceived(socketResult: GameContextRefreshEntityLookMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        if (socketResult.id == gameInfo.playerId)
            CharactersUIUtil.updateSkin(gameInfo.character, socketResult.entityLook)
    }

}