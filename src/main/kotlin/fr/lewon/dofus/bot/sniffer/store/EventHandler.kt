package fr.lewon.dofus.bot.sniffer.store

interface EventHandler<T> {

    fun onEventReceived(socketResult: T)
}