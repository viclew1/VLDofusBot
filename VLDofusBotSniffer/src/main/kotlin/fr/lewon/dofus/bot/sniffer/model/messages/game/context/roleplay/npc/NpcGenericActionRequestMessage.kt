package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.npc

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class NpcGenericActionRequestMessage : NetworkMessage() {
	var npcId: Int = 0
	var npcActionId: Int = 0
	var npcMapId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		npcId = stream.readInt().toInt()
		npcActionId = stream.readUnsignedByte().toInt()
		npcMapId = stream.readDouble().toDouble()
	}
	override fun getNetworkMessageId(): Int = 4675
}
