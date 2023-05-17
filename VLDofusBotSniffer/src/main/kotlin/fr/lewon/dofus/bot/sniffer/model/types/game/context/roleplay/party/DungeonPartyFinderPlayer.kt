package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.party

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class DungeonPartyFinderPlayer : NetworkType() {
	var playerId: Double = 0.0
	var playerName: String = ""
	var breed: Int = 0
	var sex: Boolean = false
	var level: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerId = stream.readVarLong().toDouble()
		playerName = stream.readUTF()
		breed = stream.readUnsignedByte().toInt()
		sex = stream.readBoolean()
		level = stream.readVarShort().toInt()
	}
}
