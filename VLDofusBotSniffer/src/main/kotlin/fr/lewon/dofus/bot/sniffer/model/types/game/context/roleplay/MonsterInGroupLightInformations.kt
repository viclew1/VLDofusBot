package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MonsterInGroupLightInformations : NetworkType() {
	var genericId: Int = 0
	var grade: Int = 0
	var level: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		genericId = stream.readInt().toInt()
		grade = stream.readUnsignedByte().toInt()
		level = stream.readUnsignedShort().toInt()
	}
}
