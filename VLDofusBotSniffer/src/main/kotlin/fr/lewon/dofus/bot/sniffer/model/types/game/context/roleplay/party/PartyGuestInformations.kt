package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party

import fr.lewon.dofus.bot.sniffer.model.types.game.character.status.PlayerStatus
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party.entity.PartyEntityBaseInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PartyGuestInformations : NetworkType() {
	var guestId: Double = 0.0
	var hostId: Double = 0.0
	var name: String = ""
	lateinit var guestLook: EntityLook
	var breed: Int = 0
	var sex: Boolean = false
	lateinit var status: PlayerStatus
	var entities: ArrayList<PartyEntityBaseInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		guestId = stream.readVarLong().toDouble()
		hostId = stream.readVarLong().toDouble()
		name = stream.readUTF()
		guestLook = EntityLook()
		guestLook.deserialize(stream)
		breed = stream.readUnsignedByte().toInt()
		sex = stream.readBoolean()
		status = ProtocolTypeManager.getInstance<PlayerStatus>(stream.readUnsignedShort())
		status.deserialize(stream)
		entities = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = PartyEntityBaseInformation()
			item.deserialize(stream)
			entities.add(item)
		}
	}
}
