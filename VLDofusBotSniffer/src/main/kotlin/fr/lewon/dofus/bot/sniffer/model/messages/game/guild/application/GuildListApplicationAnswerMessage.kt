package fr.lewon.dofus.bot.sniffer.model.messages.game.guild.application

import fr.lewon.dofus.bot.sniffer.model.messages.game.PaginationAnswerAbstractMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.social.application.SocialApplicationInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildListApplicationAnswerMessage : PaginationAnswerAbstractMessage() {
	var applies: ArrayList<SocialApplicationInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		applies = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = SocialApplicationInformation()
			item.deserialize(stream)
			applies.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 8273
}
