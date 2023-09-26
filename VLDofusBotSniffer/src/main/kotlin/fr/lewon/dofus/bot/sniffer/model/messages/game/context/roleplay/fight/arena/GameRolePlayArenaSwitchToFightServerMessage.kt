package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.fight.arena

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayArenaSwitchToFightServerMessage : NetworkMessage() {
	var address: String = ""
	var ports: ArrayList<Int> = ArrayList()
	var token: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		address = stream.readUTF()
		ports = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			ports.add(item)
		}
		token = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 2464
}
