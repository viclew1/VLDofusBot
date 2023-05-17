package fr.lewon.dofus.bot.sniffer.model.messages.game.presets

import fr.lewon.dofus.bot.sniffer.model.types.game.presets.Preset
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PresetsMessage : NetworkMessage() {
	var presets: ArrayList<Preset> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		presets = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<Preset>(stream.readUnsignedShort())
			item.deserialize(stream)
			presets.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 2275
}
