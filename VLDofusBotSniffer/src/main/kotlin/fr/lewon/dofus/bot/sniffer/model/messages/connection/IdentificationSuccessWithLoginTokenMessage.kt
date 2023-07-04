package fr.lewon.dofus.bot.sniffer.model.messages.connection

import fr.lewon.dofus.bot.sniffer.model.types.common.AccountTagInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IdentificationSuccessWithLoginTokenMessage : IdentificationSuccessMessage() {
	var loginToken: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		loginToken = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 6554
}
