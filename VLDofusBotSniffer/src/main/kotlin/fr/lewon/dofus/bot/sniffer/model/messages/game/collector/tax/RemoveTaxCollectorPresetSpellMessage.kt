package fr.lewon.dofus.bot.sniffer.model.messages.game.collector.tax

import fr.lewon.dofus.bot.sniffer.model.types.game.uuid
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class RemoveTaxCollectorPresetSpellMessage : NetworkMessage() {
	lateinit var presetId: uuid
	var slot: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		presetId = uuid()
		presetId.deserialize(stream)
		slot = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 8654
}
