package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.character.CharacterMinimalPlusLookInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.social.fight.SocialFightInfo
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceFightFighterAddedMessage : NetworkMessage() {
	lateinit var allianceFightInfo: SocialFightInfo
	lateinit var fighter: CharacterMinimalPlusLookInformations
	var team: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		allianceFightInfo = SocialFightInfo()
		allianceFightInfo.deserialize(stream)
		fighter = CharacterMinimalPlusLookInformations()
		fighter.deserialize(stream)
		team = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 5216
}
