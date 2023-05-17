package fr.lewon.dofus.bot.sniffer.model.types.game.alliance

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class KothWinner : NetworkType() {
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
	}
}
