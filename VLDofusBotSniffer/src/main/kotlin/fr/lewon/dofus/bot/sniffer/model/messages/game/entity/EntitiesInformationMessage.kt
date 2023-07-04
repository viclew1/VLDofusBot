package fr.lewon.dofus.bot.sniffer.model.messages.game.entity

import fr.lewon.dofus.bot.sniffer.model.types.game.entity.EntityInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class EntitiesInformationMessage : NetworkMessage() {
	var entities: ArrayList<EntityInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		entities = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = EntityInformation()
			item.deserialize(stream)
			entities.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 6656
}
