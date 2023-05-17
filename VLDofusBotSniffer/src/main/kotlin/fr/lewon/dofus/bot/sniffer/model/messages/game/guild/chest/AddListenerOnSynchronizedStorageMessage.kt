package fr.lewon.dofus.bot.sniffer.model.messages.game.guild.chest

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AddListenerOnSynchronizedStorageMessage : NetworkMessage() {
	var player: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		player = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 1219
}
