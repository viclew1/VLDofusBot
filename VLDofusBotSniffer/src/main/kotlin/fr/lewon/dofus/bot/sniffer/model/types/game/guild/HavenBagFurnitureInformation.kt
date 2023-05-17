package fr.lewon.dofus.bot.sniffer.model.types.game.guild

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HavenBagFurnitureInformation : NetworkType() {
	var cellId: Int = 0
	var funitureId: Int = 0
	var orientation: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		cellId = stream.readVarShort().toInt()
		funitureId = stream.readInt().toInt()
		orientation = stream.readUnsignedByte().toInt()
	}
}
