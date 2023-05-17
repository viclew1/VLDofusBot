package fr.lewon.dofus.bot.sniffer.model.messages.game.atlas

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.AtlasPointsInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AtlasPointInformationsMessage : NetworkMessage() {
	lateinit var type: AtlasPointsInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		type = AtlasPointsInformations()
		type.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 5642
}
