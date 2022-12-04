package fr.lewon.dofus.bot.handlers.exchange

import fr.lewon.dofus.bot.gui2.main.metamob.MetamobHelperUIUtil
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges.ExchangeTypesItemsExchangerDescriptionForUserMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.external.metamob.MetamobMonstersHelper

class ExchangeTypesItemsExchangerDescriptionForUserEventHandler :
    IEventHandler<ExchangeTypesItemsExchangerDescriptionForUserMessage> {

    override fun onEventReceived(
        socketResult: ExchangeTypesItemsExchangerDescriptionForUserMessage,
        connection: DofusConnection
    ) {
        if (MetamobMonstersHelper.isMetamobConfigured() && socketResult.objectGID == MetamobMonstersHelper.ARCHMONSTER_SOUL_STONE_ITEM_ID) {
            Thread {
                MetamobHelperUIUtil.updateArchmonsterPrices(socketResult)
            }.start()
        }
    }
}