package fr.lewon.dofus.bot.sniffer.model.types.game.paddock

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PaddockInformationsForSell : NetworkType() {
	var guildOwner: String = ""
	var worldX: Int = 0
	var worldY: Int = 0
	var subAreaId: Int = 0
	var nbMount: Int = 0
	var nbObject: Int = 0
	var price: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		guildOwner = stream.readUTF()
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
		subAreaId = stream.readVarShort().toInt()
		nbMount = stream.readUnsignedByte().toInt()
		nbObject = stream.readUnsignedByte().toInt()
		price = stream.readVarLong().toDouble()
	}
}
