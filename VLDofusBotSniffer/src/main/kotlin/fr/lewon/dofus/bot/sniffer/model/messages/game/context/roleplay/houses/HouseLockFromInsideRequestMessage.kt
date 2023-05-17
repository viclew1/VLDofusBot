package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.houses

import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.lockable.LockableChangeCodeMessage
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HouseLockFromInsideRequestMessage : LockableChangeCodeMessage() {
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 1120
}
