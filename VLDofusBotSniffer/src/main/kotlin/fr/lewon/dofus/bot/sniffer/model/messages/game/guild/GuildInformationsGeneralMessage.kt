package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildInformationsGeneralMessage : NetworkMessage() {
	var abandonnedPaddock: Boolean = false
	var level: Int = 0
	var expLevelFloor: Double = 0.0
	var experience: Double = 0.0
	var expNextLevelFloor: Double = 0.0
	var creationDate: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		abandonnedPaddock = stream.readBoolean()
		level = stream.readUnsignedByte().toInt()
		expLevelFloor = stream.readVarLong().toDouble()
		experience = stream.readVarLong().toDouble()
		expNextLevelFloor = stream.readVarLong().toDouble()
		creationDate = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 8823
}
