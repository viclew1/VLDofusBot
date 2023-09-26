package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.character.CharacterMinimalSocialPublicInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.social.GuildFactSheetInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildFactsMessage : NetworkMessage() {
	lateinit var infos: GuildFactSheetInformations
	var creationDate: Int = 0
	var members: ArrayList<CharacterMinimalSocialPublicInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		infos = ProtocolTypeManager.getInstance<GuildFactSheetInformations>(stream.readUnsignedShort())
		infos.deserialize(stream)
		creationDate = stream.readInt().toInt()
		members = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = CharacterMinimalSocialPublicInformations()
			item.deserialize(stream)
			members.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 2978
}
