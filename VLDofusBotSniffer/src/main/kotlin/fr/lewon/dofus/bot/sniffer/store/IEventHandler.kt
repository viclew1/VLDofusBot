package fr.lewon.dofus.bot.sniffer.store

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage

interface IEventHandler<T : NetworkMessage> {

    fun onEventReceived(socketResult: T, connection: DofusConnection)

}