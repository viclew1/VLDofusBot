package fr.lewon.dofus.bot.sniffer.model.types.game.mount

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class UpdateMountBooleanCharacteristic : UpdateMountCharacteristic() {
	var value: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		value = stream.readBoolean()
	}
}
