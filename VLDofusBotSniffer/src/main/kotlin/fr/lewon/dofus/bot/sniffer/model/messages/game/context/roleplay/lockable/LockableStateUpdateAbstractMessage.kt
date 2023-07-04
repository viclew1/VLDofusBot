package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.lockable

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class LockableStateUpdateAbstractMessage : NetworkMessage() {
	var locked: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		locked = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 6940
}
