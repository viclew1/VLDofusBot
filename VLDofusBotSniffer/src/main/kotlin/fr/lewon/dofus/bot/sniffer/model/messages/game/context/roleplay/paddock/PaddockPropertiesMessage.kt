package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.paddock

import fr.lewon.dofus.bot.sniffer.model.types.game.paddock.PaddockInstancesInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PaddockPropertiesMessage : NetworkMessage() {
	lateinit var properties: PaddockInstancesInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		properties = PaddockInstancesInformations()
		properties.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 4292
}
