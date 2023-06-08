package fr.lewon.dofus.bot.handlers.characteristics

import fr.lewon.dofus.bot.gui.main.scripts.characters.edit.global.CharacterGlobalInformationUIUtil
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items.InventoryWeightMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object InventoryWeightEventHandler : IEventHandler<InventoryWeightMessage> {
    override fun onEventReceived(socketResult: InventoryWeightMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.weightMax = socketResult.weightMax
        gameInfo.inventoryWeight = socketResult.inventoryWeight
        CharacterGlobalInformationUIUtil.updateCharacterWeight(
            gameInfo.character.name,
            socketResult.inventoryWeight,
            socketResult.weightMax
        )
    }
}