package fr.lewon.dofus.bot.sniffer.model.types.game.character.alignment

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ActorAlignmentInformations : NetworkType() {
	var alignmentSide: Int = 0
	var alignmentValue: Int = 0
	var alignmentGrade: Int = 0
	var characterPower: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		alignmentSide = stream.readUnsignedByte().toInt()
		alignmentValue = stream.readUnsignedByte().toInt()
		alignmentGrade = stream.readUnsignedByte().toInt()
		characterPower = stream.readDouble().toDouble()
	}
}
