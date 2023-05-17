package fr.lewon.dofus.bot.sniffer.model.types.common

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AccountTagInformation : NetworkType() {
	var nickname: String = ""
	var tagNumber: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		nickname = stream.readUTF()
		tagNumber = stream.readUTF()
	}
}
