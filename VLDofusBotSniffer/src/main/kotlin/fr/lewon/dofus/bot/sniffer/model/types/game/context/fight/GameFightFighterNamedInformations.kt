package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.character.status.PlayerStatus
import fr.lewon.dofus.bot.sniffer.model.types.game.context.EntityDispositionInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightFighterNamedInformations : GameFightFighterInformations() {
	var name: String = ""
	lateinit var status: PlayerStatus
	var leagueId: Int = 0
	var ladderPosition: Int = 0
	var hiddenInPrefight: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		name = stream.readUTF()
		status = PlayerStatus()
		status.deserialize(stream)
		leagueId = stream.readVarShort().toInt()
		ladderPosition = stream.readInt().toInt()
		hiddenInPrefight = stream.readBoolean()
	}
}
