package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.GameFightFighterLightInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MapRunningFightDetailsMessage : NetworkMessage() {
	var fightId: Int = 0
	var attackers: ArrayList<GameFightFighterLightInformations> = ArrayList()
	var defenders: ArrayList<GameFightFighterLightInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		fightId = stream.readVarShort().toInt()
		attackers = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<GameFightFighterLightInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			attackers.add(item)
		}
		defenders = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<GameFightFighterLightInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			defenders.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 857
}
