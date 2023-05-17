package fr.lewon.dofus.bot.sniffer.model.messages.game.presets

import fr.lewon.dofus.bot.sniffer.model.types.game.presets.ItemForPreset
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ItemForPresetUpdateMessage : NetworkMessage() {
	var presetId: Int = 0
	lateinit var presetItem: ItemForPreset
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		presetId = stream.readUnsignedShort().toInt()
		presetItem = ItemForPreset()
		presetItem.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 641
}
