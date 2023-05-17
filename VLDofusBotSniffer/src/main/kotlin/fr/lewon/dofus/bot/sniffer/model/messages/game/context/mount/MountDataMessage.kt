package fr.lewon.dofus.bot.sniffer.model.messages.game.context.mount

import fr.lewon.dofus.bot.sniffer.model.types.game.mount.MountClientData
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MountDataMessage : NetworkMessage() {
	lateinit var mountData: MountClientData
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		mountData = MountClientData()
		mountData.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 1532
}
