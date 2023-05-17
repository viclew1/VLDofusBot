package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party

import fr.lewon.dofus.bot.sniffer.model.types.game.character.choice.CharacterBaseInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.entity.PartyEntityBaseInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyInvitationMemberInformations : CharacterBaseInformations() {
	var worldX: Int = 0
	var worldY: Int = 0
	var mapId: Double = 0.0
	var subAreaId: Int = 0
	var entities: ArrayList<PartyEntityBaseInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
		mapId = stream.readDouble().toDouble()
		subAreaId = stream.readVarShort().toInt()
		entities = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = PartyEntityBaseInformation()
			item.deserialize(stream)
			entities.add(item)
		}
	}
}
