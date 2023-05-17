package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightPhase
import fr.lewon.dofus.bot.sniffer.model.types.game.social.fight.SocialFightInfo
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceFightPhaseUpdateMessage : NetworkMessage() {
	lateinit var allianceFightInfo: SocialFightInfo
	lateinit var newPhase: FightPhase
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		allianceFightInfo = SocialFightInfo()
		allianceFightInfo.deserialize(stream)
		newPhase = FightPhase()
		newPhase.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 5840
}
