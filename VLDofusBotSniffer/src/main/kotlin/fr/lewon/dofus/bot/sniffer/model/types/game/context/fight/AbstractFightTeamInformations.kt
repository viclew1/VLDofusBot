package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AbstractFightTeamInformations : NetworkType() {
	var teamId: Int = 0
	var leaderId: Double = 0.0
	var teamSide: Int = 0
	var teamTypeId: Int = 0
	var nbWaves: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		teamId = stream.readUnsignedByte().toInt()
		leaderId = stream.readDouble().toDouble()
		teamSide = stream.readUnsignedByte().toInt()
		teamTypeId = stream.readUnsignedByte().toInt()
		nbWaves = stream.readUnsignedByte().toInt()
	}
}
