package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.havenbag

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HavenBagDailyLoteryMessage : NetworkMessage() {
	var returnType: Int = 0
	var gameActionId: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		returnType = stream.readUnsignedByte().toInt()
		gameActionId = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 4338
}
