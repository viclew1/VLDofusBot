package fr.lewon.dofus.bot.sniffer.model.messages.game.character.alteration

import fr.lewon.dofus.bot.sniffer.model.types.game.character.alteration.AlterationInfo
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AlterationsMessage : NetworkMessage() {
	var alterations: ArrayList<AlterationInfo> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		alterations = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = AlterationInfo()
			item.deserialize(stream)
			alterations.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9238
}
