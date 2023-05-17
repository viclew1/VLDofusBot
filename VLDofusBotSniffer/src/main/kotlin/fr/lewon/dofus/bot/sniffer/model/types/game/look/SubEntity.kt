package fr.lewon.dofus.bot.sniffer.model.types.game.look

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SubEntity : NetworkType() {
	var bindingPointCategory: Int = 0
	var bindingPointIndex: Int = 0
	lateinit var subEntityLook: EntityLook
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		bindingPointCategory = stream.readUnsignedByte().toInt()
		bindingPointIndex = stream.readUnsignedByte().toInt()
		subEntityLook = EntityLook()
		subEntityLook.deserialize(stream)
	}
}
