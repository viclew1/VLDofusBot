package fr.lewon.dofus.bot.sniffer.model.types.game.character.guild.note

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PlayerNote : NetworkType() {
	var content: String = ""
	var lastEditDate: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		content = stream.readUTF()
		lastEditDate = stream.readDouble().toDouble()
	}
}
