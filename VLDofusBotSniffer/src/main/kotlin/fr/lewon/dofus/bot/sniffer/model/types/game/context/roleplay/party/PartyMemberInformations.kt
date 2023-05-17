package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party

import fr.lewon.dofus.bot.sniffer.model.types.game.character.choice.CharacterBaseInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.character.status.PlayerStatus
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.entity.PartyEntityBaseInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyMemberInformations : CharacterBaseInformations() {
	var lifePoints: Int = 0
	var maxLifePoints: Int = 0
	var prospecting: Int = 0
	var regenRate: Int = 0
	var initiative: Int = 0
	var alignmentSide: Int = 0
	var worldX: Int = 0
	var worldY: Int = 0
	var mapId: Double = 0.0
	var subAreaId: Int = 0
	lateinit var status: PlayerStatus
	var entities: ArrayList<PartyEntityBaseInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		lifePoints = stream.readVarInt().toInt()
		maxLifePoints = stream.readVarInt().toInt()
		prospecting = stream.readVarInt().toInt()
		regenRate = stream.readUnsignedByte().toInt()
		initiative = stream.readVarInt().toInt()
		alignmentSide = stream.readUnsignedByte().toInt()
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
		mapId = stream.readDouble().toDouble()
		subAreaId = stream.readVarShort().toInt()
		status = ProtocolTypeManager.getInstance<PlayerStatus>(stream.readUnsignedShort())
		status.deserialize(stream)
		entities = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<PartyEntityBaseInformation>(stream.readUnsignedShort())
			item.deserialize(stream)
			entities.add(item)
		}
	}
}
