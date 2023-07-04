package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.GameFightFighterInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightSynchronizeMessage : NetworkMessage() {
	var fighters: ArrayList<GameFightFighterInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		fighters = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<GameFightFighterInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			fighters.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4483
}
