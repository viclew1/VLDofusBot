package fr.lewon.dofus.bot.sniffer.model.types.game.rank

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class RankMinimalInformation : NetworkType() {
	var id: Int = 0
	var name: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readVarInt().toInt()
		name = stream.readUTF()
	}
}
