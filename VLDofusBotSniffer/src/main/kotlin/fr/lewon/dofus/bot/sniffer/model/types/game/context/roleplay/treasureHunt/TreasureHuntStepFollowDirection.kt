package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.treasureHunt

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TreasureHuntStepFollowDirection : TreasureHuntStep() {
	var direction: Int = 0
	var mapCount: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		direction = stream.readUnsignedByte().toInt()
		mapCount = stream.readVarShort().toInt()
	}
}
