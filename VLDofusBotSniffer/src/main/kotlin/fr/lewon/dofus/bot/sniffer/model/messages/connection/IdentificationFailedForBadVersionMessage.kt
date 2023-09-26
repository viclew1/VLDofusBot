package fr.lewon.dofus.bot.sniffer.model.messages.connection

import fr.lewon.dofus.bot.sniffer.model.types.version.Version
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IdentificationFailedForBadVersionMessage : IdentificationFailedMessage() {
	lateinit var requiredVersion: Version
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		requiredVersion = Version()
		requiredVersion.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 7224
}
