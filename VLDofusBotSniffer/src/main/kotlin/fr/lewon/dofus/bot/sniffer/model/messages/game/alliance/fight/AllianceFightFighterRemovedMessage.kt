package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.social.fight.SocialFightInfo
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceFightFighterRemovedMessage : NetworkMessage() {
	lateinit var allianceFightInfo: SocialFightInfo
	var fighterId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		allianceFightInfo = SocialFightInfo()
		allianceFightInfo.deserialize(stream)
		fighterId = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 5651
}
