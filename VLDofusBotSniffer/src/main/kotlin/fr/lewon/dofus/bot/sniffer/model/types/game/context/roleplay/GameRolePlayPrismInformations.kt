package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.context.EntityDispositionInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.sniffer.model.types.game.prism.PrismInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayPrismInformations : GameRolePlayActorInformations() {
	lateinit var prism: PrismInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		prism = ProtocolTypeManager.getInstance<PrismInformation>(stream.readUnsignedShort())
		prism.deserialize(stream)
	}
}
