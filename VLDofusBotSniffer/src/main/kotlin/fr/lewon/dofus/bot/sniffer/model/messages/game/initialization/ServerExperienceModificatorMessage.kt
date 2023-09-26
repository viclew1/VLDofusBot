package fr.lewon.dofus.bot.sniffer.model.messages.game.initialization

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ServerExperienceModificatorMessage : NetworkMessage() {
	var experiencePercent: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		experiencePercent = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 1898
}
