package fr.lewon.dofus.bot.sniffer.parser

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.store.EventStore

data class MessageParserState(
    val connection: DofusConnection,
    val eventStore: EventStore,
    val logger: VldbLogger,
)