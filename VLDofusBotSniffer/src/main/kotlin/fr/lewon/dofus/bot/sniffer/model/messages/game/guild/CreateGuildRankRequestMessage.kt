package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CreateGuildRankRequestMessage : NetworkMessage() {
	var parentRankId: Int = 0
	var gfxId: Int = 0
	var name: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		parentRankId = stream.readVarInt().toInt()
		gfxId = stream.readVarInt().toInt()
		name = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 720
}
