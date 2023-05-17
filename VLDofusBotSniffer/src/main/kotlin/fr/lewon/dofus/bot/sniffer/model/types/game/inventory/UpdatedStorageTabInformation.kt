package fr.lewon.dofus.bot.sniffer.model.types.game.inventory

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class UpdatedStorageTabInformation : NetworkType() {
	var name: String = ""
	var tabNumber: Int = 0
	var picto: Int = 0
	var dropTypeLimitation: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		name = stream.readUTF()
		tabNumber = stream.readVarInt().toInt()
		picto = stream.readVarInt().toInt()
		dropTypeLimitation = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			dropTypeLimitation.add(item)
		}
	}
}
