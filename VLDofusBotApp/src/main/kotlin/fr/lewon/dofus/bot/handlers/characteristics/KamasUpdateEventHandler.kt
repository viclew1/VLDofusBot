package fr.lewon.dofus.bot.handlers.characteristics

import fr.lewon.dofus.bot.gui.main.scripts.characters.edit.global.CharacterGlobalInformationUIUtil
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.KamasUpdateMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object KamasUpdateEventHandler : IEventHandler<KamasUpdateMessage> {
    override fun onEventReceived(socketResult: KamasUpdateMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        CharacterGlobalInformationUIUtil.updateCharacterKamas(gameInfo.character.name, socketResult.kamasTotal.toLong())
    }
}