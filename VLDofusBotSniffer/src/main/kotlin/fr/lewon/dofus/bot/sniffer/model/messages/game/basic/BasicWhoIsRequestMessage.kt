package fr.lewon.dofus.bot.sniffer.model.messages.game.basic

import fr.lewon.dofus.bot.sniffer.model.types.common.AbstractPlayerSearchInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BasicWhoIsRequestMessage : NetworkMessage() {
	var verbose: Boolean = false
	lateinit var target: AbstractPlayerSearchInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		verbose = stream.readBoolean()
		target = ProtocolTypeManager.getInstance<AbstractPlayerSearchInformation>(stream.readUnsignedShort())
		target.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 2823
}
