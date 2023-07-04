package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.havenbag

import fr.lewon.dofus.bot.sniffer.model.types.game.havenbag.HavenBagRoomPreviewInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HavenBagRoomUpdateMessage : NetworkMessage() {
	var action: Int = 0
	var roomsPreview: ArrayList<HavenBagRoomPreviewInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		action = stream.readUnsignedByte().toInt()
		roomsPreview = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = HavenBagRoomPreviewInformation()
			item.deserialize(stream)
			roomsPreview.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9458
}
