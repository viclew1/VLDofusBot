package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.context.GameContextActorPositionInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameContextBasicSpawnInformation : NetworkType() {
	var teamId: Int = 0
	var alive: Boolean = false
	lateinit var informations: GameContextActorPositionInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		teamId = stream.readUnsignedByte().toInt()
		alive = stream.readBoolean()
		informations = ProtocolTypeManager.getInstance<GameContextActorPositionInformations>(stream.readUnsignedShort())
		informations.deserialize(stream)
	}
}
