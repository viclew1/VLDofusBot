package fr.lewon.dofus.bot.sniffer.model.messages.game.presets

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IconPresetSaveRequestMessage : NetworkMessage() {
	var presetId: Int = 0
	var symbolId: Int = 0
	var updateData: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		presetId = stream.readUnsignedShort().toInt()
		symbolId = stream.readUnsignedByte().toInt()
		updateData = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 4212
}
