package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.entity

import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyEntityBaseInformation : NetworkType() {
	var indexId: Int = 0
	var entityModelId: Int = 0
	lateinit var entityLook: EntityLook
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		indexId = stream.readUnsignedByte().toInt()
		entityModelId = stream.readUnsignedByte().toInt()
		entityLook = EntityLook()
		entityLook.deserialize(stream)
	}
}
