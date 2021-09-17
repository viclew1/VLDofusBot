package fr.lewon.dofus.bot.sniffer.store

import fr.lewon.dofus.bot.sniffer.model.INetworkType

interface EventHandler<T : INetworkType> {

    fun onEventReceived(socketResult: T)

}