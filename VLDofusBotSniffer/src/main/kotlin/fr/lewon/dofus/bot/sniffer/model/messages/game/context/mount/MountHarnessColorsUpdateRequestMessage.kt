package fr.lewon.dofus.bot.sniffer.model.messages.game.context.mount

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MountHarnessColorsUpdateRequestMessage : NetworkMessage() {
	var useHarnessColors: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		useHarnessColors = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 4991
}
