package fr.lewon.dofus.bot.sniffer.model.messages.game.context.dungeon

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class DungeonKeyRingUpdateMessage : NetworkMessage() {
	var dungeonId: Int = 0
	var available: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		dungeonId = stream.readVarShort().toInt()
		available = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 6442
}
