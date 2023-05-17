package fr.lewon.dofus.bot.sniffer.model.types.game.social

import fr.lewon.dofus.bot.sniffer.model.types.game.character.CharacterMinimalInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.character.status.PlayerStatus
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SocialMember : CharacterMinimalInformations() {
	var breed: Int = 0
	var sex: Boolean = false
	var connected: Int = 0
	var hoursSinceLastConnection: Int = 0
	var accountId: Int = 0
	lateinit var status: PlayerStatus
	var rankId: Int = 0
	var enrollmentDate: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		breed = stream.readUnsignedByte().toInt()
		sex = stream.readBoolean()
		connected = stream.readUnsignedByte().toInt()
		hoursSinceLastConnection = stream.readUnsignedShort().toInt()
		accountId = stream.readInt().toInt()
		status = ProtocolTypeManager.getInstance<PlayerStatus>(stream.readUnsignedShort())
		status.deserialize(stream)
		rankId = stream.readInt().toInt()
		enrollmentDate = stream.readDouble().toDouble()
	}
}
