package fr.lewon.dofus.bot.sniffer.model.types.game.havenbag

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HavenBagRoomPreviewInformation : NetworkType() {
	var roomId: Int = 0
	var themeId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		roomId = stream.readUnsignedByte().toInt()
		themeId = stream.readUnsignedByte().toInt()
	}
}
