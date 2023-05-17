package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance

import fr.lewon.dofus.bot.sniffer.model.types.game.alliance.KothWinner
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class KothEndMessage : NetworkMessage() {
	lateinit var winner: KothWinner
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		winner = KothWinner()
		winner.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 173
}
