package fr.lewon.dofus.bot.handlers.exchange

import fr.lewon.dofus.bot.gui.main.auctionhouse.AuctionHouseItemFinderUIUtil
import fr.lewon.dofus.bot.gui.main.metamob.MetamobHelperUIUtil
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges.ExchangeTypesItemsExchangerDescriptionForUserMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.ids.ItemIds

object ExchangeTypesItemsExchangerDescriptionForUserEventHandler :
    IEventHandler<ExchangeTypesItemsExchangerDescriptionForUserMessage> {

    override fun onEventReceived(
        socketResult: ExchangeTypesItemsExchangerDescriptionForUserMessage,
        connection: DofusConnection
    ) {
        if (socketResult.objectGID == ItemIds.ARCHMONSTER_SOUL_STONE_ITEM_ID) {
            updateMetamobHelper(socketResult)
        } else {
            updateAuctionHouseItemFinder(socketResult)
        }
    }

    private fun updateAuctionHouseItemFinder(socketResult: ExchangeTypesItemsExchangerDescriptionForUserMessage) {
        Thread {
            AuctionHouseItemFinderUIUtil.updateItem(
                socketResult.objectGID,
                socketResult.itemTypeDescriptions
            )
        }.start()
    }

    private fun updateMetamobHelper(socketResult: ExchangeTypesItemsExchangerDescriptionForUserMessage) {
        Thread { MetamobHelperUIUtil.updateArchmonsterPrices(socketResult) }.start()
    }
}