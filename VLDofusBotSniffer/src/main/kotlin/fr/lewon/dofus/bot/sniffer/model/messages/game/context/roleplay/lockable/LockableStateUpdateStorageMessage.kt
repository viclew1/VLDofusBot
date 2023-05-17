package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.lockable

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class LockableStateUpdateStorageMessage : LockableStateUpdateAbstractMessage() {
	var mapId: Double = 0.0
	var elementId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		mapId = stream.readDouble().toDouble()
		elementId = stream.readVarInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 4602
}
