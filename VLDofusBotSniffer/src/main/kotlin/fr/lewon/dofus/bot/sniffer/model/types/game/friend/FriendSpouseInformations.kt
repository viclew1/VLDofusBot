package fr.lewon.dofus.bot.sniffer.model.types.game.friend

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GuildInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FriendSpouseInformations : NetworkType() {
	var spouseAccountId: Int = 0
	var spouseId: Double = 0.0
	var spouseName: String = ""
	var spouseLevel: Int = 0
	var breed: Int = 0
	var sex: Int = 0
	lateinit var spouseEntityLook: EntityLook
	lateinit var guildInfo: GuildInformations
	var alignmentSide: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		spouseAccountId = stream.readInt().toInt()
		spouseId = stream.readVarLong().toDouble()
		spouseName = stream.readUTF()
		spouseLevel = stream.readVarShort().toInt()
		breed = stream.readUnsignedByte().toInt()
		sex = stream.readUnsignedByte().toInt()
		spouseEntityLook = EntityLook()
		spouseEntityLook.deserialize(stream)
		guildInfo = GuildInformations()
		guildInfo.deserialize(stream)
		alignmentSide = stream.readUnsignedByte().toInt()
	}
}
