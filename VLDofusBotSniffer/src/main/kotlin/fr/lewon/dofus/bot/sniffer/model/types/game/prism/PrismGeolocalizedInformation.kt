package fr.lewon.dofus.bot.sniffer.model.types.game.prism

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PrismGeolocalizedInformation : NetworkType() {
	var subAreaId: Int = 0
	var allianceId: Int = 0
	var worldX: Int = 0
	var worldY: Int = 0
	var mapId: Double = 0.0
	lateinit var prism: PrismInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		subAreaId = stream.readVarShort().toInt()
		allianceId = stream.readVarInt().toInt()
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
		mapId = stream.readDouble().toDouble()
		prism = ProtocolTypeManager.getInstance<PrismInformation>(stream.readUnsignedShort())
		prism.deserialize(stream)
	}
}
