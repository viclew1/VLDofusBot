package fr.lewon.dofus.bot.sniffer.model.types.game.friend

import fr.lewon.dofus.bot.sniffer.model.types.common.AccountTagInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IgnoredOnlineInformations : IgnoredInformations() {
	var playerId: Double = 0.0
	var playerName: String = ""
	var breed: Int = 0
	var sex: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerId = stream.readVarLong().toDouble()
		playerName = stream.readUTF()
		breed = stream.readUnsignedByte().toInt()
		sex = stream.readBoolean()
	}
}
