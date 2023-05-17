package fr.lewon.dofus.bot.sniffer.model.types.game.social.application

import fr.lewon.dofus.bot.sniffer.model.types.game.character.status.PlayerStatus
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ApplicationPlayerInformation : NetworkType() {
	var playerId: Double = 0.0
	var playerName: String = ""
	var breed: Int = 0
	var sex: Boolean = false
	var level: Int = 0
	var accountId: Int = 0
	var accountTag: String = ""
	var accountNickname: String = ""
	lateinit var status: PlayerStatus
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerId = stream.readVarLong().toDouble()
		playerName = stream.readUTF()
		breed = stream.readUnsignedByte().toInt()
		sex = stream.readBoolean()
		level = stream.readVarInt().toInt()
		accountId = stream.readVarInt().toInt()
		accountTag = stream.readUTF()
		accountNickname = stream.readUTF()
		status = PlayerStatus()
		status.deserialize(stream)
	}
}
