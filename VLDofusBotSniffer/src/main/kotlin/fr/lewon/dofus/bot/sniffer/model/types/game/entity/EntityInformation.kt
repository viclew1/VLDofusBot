package fr.lewon.dofus.bot.sniffer.model.types.game.entity

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class EntityInformation : NetworkType() {
	var id: Int = 0
	var experience: Int = 0
	var status: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readVarShort().toInt()
		experience = stream.readVarInt().toInt()
		status = stream.readBoolean()
	}
}
