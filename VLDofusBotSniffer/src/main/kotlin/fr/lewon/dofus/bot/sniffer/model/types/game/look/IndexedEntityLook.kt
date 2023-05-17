package fr.lewon.dofus.bot.sniffer.model.types.game.look

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IndexedEntityLook : NetworkType() {
	lateinit var look: EntityLook
	var index: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		look = EntityLook()
		look.deserialize(stream)
		index = stream.readUnsignedByte().toInt()
	}
}
