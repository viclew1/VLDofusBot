package fr.lewon.dofus.bot.sniffer.model.types.game.friend

import fr.lewon.dofus.bot.sniffer.model.types.common.AccountTagInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.character.status.PlayerStatus
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GuildInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FriendOnlineInformations : FriendInformations() {
	var sex: Boolean = false
	var havenBagShared: Boolean = false
	var playerId: Double = 0.0
	var playerName: String = ""
	var level: Int = 0
	var alignmentSide: Int = 0
	var breed: Int = 0
	lateinit var guildInfo: GuildInformations
	var moodSmileyId: Int = 0
	lateinit var status: PlayerStatus
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		sex = BooleanByteWrapper.getFlag(_box0, 0)
		havenBagShared = BooleanByteWrapper.getFlag(_box0, 1)
		playerId = stream.readVarLong().toDouble()
		playerName = stream.readUTF()
		level = stream.readVarShort().toInt()
		alignmentSide = stream.readUnsignedByte().toInt()
		breed = stream.readUnsignedByte().toInt()
		guildInfo = GuildInformations()
		guildInfo.deserialize(stream)
		moodSmileyId = stream.readVarShort().toInt()
		status = ProtocolTypeManager.getInstance<PlayerStatus>(stream.readUnsignedShort())
		status.deserialize(stream)
	}
}
