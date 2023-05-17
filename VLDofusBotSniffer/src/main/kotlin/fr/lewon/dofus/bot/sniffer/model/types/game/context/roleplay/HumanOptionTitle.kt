package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HumanOptionTitle : HumanOption() {
	var titleId: Int = 0
	var titleParam: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		titleId = stream.readVarShort().toInt()
		titleParam = stream.readUTF()
	}
}
