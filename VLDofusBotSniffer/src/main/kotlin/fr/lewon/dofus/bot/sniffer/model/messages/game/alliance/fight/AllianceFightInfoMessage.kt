package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.social.fight.SocialFight
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceFightInfoMessage : NetworkMessage() {
	var allianceFights: ArrayList<SocialFight> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		allianceFights = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = SocialFight()
			item.deserialize(stream)
			allianceFights.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1055
}
