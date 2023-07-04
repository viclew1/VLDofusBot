package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.emote

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class EmotePlayMassiveMessage : EmotePlayAbstractMessage() {
	var actorIds: ArrayList<Double> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		actorIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readDouble().toDouble()
			actorIds.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 5777
}
