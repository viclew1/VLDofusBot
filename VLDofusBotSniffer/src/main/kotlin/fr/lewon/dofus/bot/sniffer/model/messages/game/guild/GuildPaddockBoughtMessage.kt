package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.paddock.PaddockContentInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildPaddockBoughtMessage : NetworkMessage() {
	lateinit var paddockInfo: PaddockContentInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		paddockInfo = PaddockContentInformations()
		paddockInfo.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 8362
}
