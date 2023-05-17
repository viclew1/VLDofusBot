package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance

import fr.lewon.dofus.bot.sniffer.model.messages.game.social.SocialNoticeSetRequestMessage
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceMotdSetRequestMessage : SocialNoticeSetRequestMessage() {
	var content: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		content = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 7489
}
