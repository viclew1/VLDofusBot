package fr.lewon.dofus.bot.sniffer.model.messages.game.approach

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AccountCapabilitiesMessage : NetworkMessage() {
	var tutorialAvailable: Boolean = false
	var canCreateNewCharacter: Boolean = false
	var accountId: Int = 0
	var status: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		tutorialAvailable = BooleanByteWrapper.getFlag(_box0, 0)
		canCreateNewCharacter = BooleanByteWrapper.getFlag(_box0, 1)
		accountId = stream.readInt().toInt()
		status = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 1358
}
