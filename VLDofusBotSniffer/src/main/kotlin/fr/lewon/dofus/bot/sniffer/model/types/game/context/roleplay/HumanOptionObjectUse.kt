package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HumanOptionObjectUse : HumanOption() {
	var delayTypeId: Int = 0
	var delayEndTime: Double = 0.0
	var objectGID: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		delayTypeId = stream.readUnsignedByte().toInt()
		delayEndTime = stream.readDouble().toDouble()
		objectGID = stream.readVarInt().toInt()
	}
}
