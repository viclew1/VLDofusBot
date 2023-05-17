package fr.lewon.dofus.bot.sniffer.model.types.game.friend

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GuildInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FriendSpouseOnlineInformations : FriendSpouseInformations() {
	var inFight: Boolean = false
	var followSpouse: Boolean = false
	var mapId: Double = 0.0
	var subAreaId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		inFight = BooleanByteWrapper.getFlag(_box0, 0)
		followSpouse = BooleanByteWrapper.getFlag(_box0, 1)
		mapId = stream.readDouble().toDouble()
		subAreaId = stream.readVarShort().toInt()
	}
}
