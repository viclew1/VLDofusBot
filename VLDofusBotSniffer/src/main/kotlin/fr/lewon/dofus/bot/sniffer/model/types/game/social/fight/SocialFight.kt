package fr.lewon.dofus.bot.sniffer.model.types.game.social.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.character.CharacterMinimalPlusLookInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightPhase
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SocialFight : NetworkType() {
	lateinit var socialFightInfo: SocialFightInfo
	var attackers: ArrayList<CharacterMinimalPlusLookInformations> = ArrayList()
	var defenders: ArrayList<CharacterMinimalPlusLookInformations> = ArrayList()
	lateinit var phase: FightPhase
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		socialFightInfo = SocialFightInfo()
		socialFightInfo.deserialize(stream)
		attackers = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = CharacterMinimalPlusLookInformations()
			item.deserialize(stream)
			attackers.add(item)
		}
		defenders = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = CharacterMinimalPlusLookInformations()
			item.deserialize(stream)
			defenders.add(item)
		}
		phase = FightPhase()
		phase.deserialize(stream)
	}
}
