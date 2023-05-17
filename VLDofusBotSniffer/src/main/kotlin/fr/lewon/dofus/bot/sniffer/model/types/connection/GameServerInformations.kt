package fr.lewon.dofus.bot.sniffer.model.types.connection

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameServerInformations : NetworkType() {
	var isMonoAccount: Boolean = false
	var isSelectable: Boolean = false
	var id: Int = 0
	var type: Int = 0
	var status: Int = 0
	var completion: Int = 0
	var charactersCount: Int = 0
	var charactersSlots: Int = 0
	var date: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		isMonoAccount = BooleanByteWrapper.getFlag(_box0, 0)
		isSelectable = BooleanByteWrapper.getFlag(_box0, 1)
		id = stream.readVarShort().toInt()
		type = stream.readUnsignedByte().toInt()
		status = stream.readUnsignedByte().toInt()
		completion = stream.readUnsignedByte().toInt()
		charactersCount = stream.readUnsignedByte().toInt()
		charactersSlots = stream.readUnsignedByte().toInt()
		date = stream.readDouble().toDouble()
	}
}
