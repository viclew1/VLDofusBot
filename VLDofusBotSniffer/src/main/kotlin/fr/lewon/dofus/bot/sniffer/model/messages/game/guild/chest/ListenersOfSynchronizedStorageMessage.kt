package fr.lewon.dofus.bot.sniffer.model.messages.game.guild.chest

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ListenersOfSynchronizedStorageMessage : NetworkMessage() {
	var players: ArrayList<String> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		players = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUTF()
			players.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4233
}
