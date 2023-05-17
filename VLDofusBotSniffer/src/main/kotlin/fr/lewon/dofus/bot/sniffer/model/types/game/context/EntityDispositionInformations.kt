package fr.lewon.dofus.bot.sniffer.model.types.game.context

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class EntityDispositionInformations : NetworkType() {
	var cellId: Int = 0
	var direction: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		cellId = stream.readUnsignedShort().toInt()
		direction = stream.readUnsignedByte().toInt()
	}
}
