package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.character

import fr.lewon.dofus.bot.sniffer.model.types.game.context.GameContextActorInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightRefreshFighterMessage : NetworkMessage() {
	lateinit var informations: GameContextActorInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		informations = ProtocolTypeManager.getInstance<GameContextActorInformations>(stream.readUnsignedShort())
		informations.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 6382
}
