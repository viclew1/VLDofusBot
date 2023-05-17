package fr.lewon.dofus.bot.sniffer.model.types.game.character.alignment

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ActorExtendedAlignmentInformations : ActorAlignmentInformations() {
	var honor: Int = 0
	var honorGradeFloor: Int = 0
	var honorNextGradeFloor: Int = 0
	var aggressable: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		honor = stream.readVarShort().toInt()
		honorGradeFloor = stream.readVarShort().toInt()
		honorNextGradeFloor = stream.readVarShort().toInt()
		aggressable = stream.readUnsignedByte().toInt()
	}
}
