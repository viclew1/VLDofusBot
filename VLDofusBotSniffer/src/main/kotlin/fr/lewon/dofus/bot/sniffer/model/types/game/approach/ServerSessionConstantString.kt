package fr.lewon.dofus.bot.sniffer.model.types.game.approach

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ServerSessionConstantString : ServerSessionConstant() {
	var value: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		value = stream.readUTF()
	}
}
