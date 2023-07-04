package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.arena

import fr.lewon.dofus.bot.sniffer.model.types.game.character.CharacterBasicMinimalInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ArenaFighterLeaveMessage : NetworkMessage() {
	lateinit var leaver: CharacterBasicMinimalInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		leaver = CharacterBasicMinimalInformations()
		leaver.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 8380
}
