package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.party

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyUpdateLightMessage : AbstractPartyEventMessage() {
	var id: Double = 0.0
	var lifePoints: Int = 0
	var maxLifePoints: Int = 0
	var prospecting: Int = 0
	var regenRate: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readVarLong().toDouble()
		lifePoints = stream.readVarInt().toInt()
		maxLifePoints = stream.readVarInt().toInt()
		prospecting = stream.readVarInt().toInt()
		regenRate = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 4567
}
