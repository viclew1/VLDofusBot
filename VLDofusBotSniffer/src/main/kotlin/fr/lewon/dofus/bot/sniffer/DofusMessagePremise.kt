package fr.lewon.dofus.bot.sniffer

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage

class DofusMessagePremise(
    val eventId: Int,
    val eventClass: Class<out NetworkMessage>,
    val stream: ByteArrayReader
)