package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.npc

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class EntityTalkMessage : NetworkMessage() {
	var entityId: Double = 0.0
	var textId: Int = 0
	var parameters: ArrayList<String> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		entityId = stream.readDouble().toDouble()
		textId = stream.readVarShort().toInt()
		parameters = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUTF()
			parameters.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9057
}
