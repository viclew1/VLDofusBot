package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightExternalInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MapRunningFightListMessage : NetworkMessage() {
	var fights: ArrayList<FightExternalInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		fights = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = FightExternalInformations()
			item.deserialize(stream)
			fights.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4622
}
