package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.entity

import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyEntityMemberInformation : PartyEntityBaseInformation() {
	var initiative: Int = 0
	var lifePoints: Int = 0
	var maxLifePoints: Int = 0
	var prospecting: Int = 0
	var regenRate: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		initiative = stream.readVarInt().toInt()
		lifePoints = stream.readVarInt().toInt()
		maxLifePoints = stream.readVarInt().toInt()
		prospecting = stream.readVarInt().toInt()
		regenRate = stream.readUnsignedByte().toInt()
	}
}
