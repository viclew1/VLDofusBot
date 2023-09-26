package fr.lewon.dofus.bot.sniffer.model.messages.game.entity

import fr.lewon.dofus.bot.sniffer.model.types.game.entity.EntityInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class EntityInformationMessage : NetworkMessage() {
	lateinit var entity: EntityInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		entity = EntityInformation()
		entity.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 3235
}
