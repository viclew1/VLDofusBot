package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.context.EntityDispositionInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.treasureHunt.PortalInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayPortalInformations : GameRolePlayActorInformations() {
	lateinit var portal: PortalInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		portal = ProtocolTypeManager.getInstance<PortalInformation>(stream.readUnsignedShort())
		portal.deserialize(stream)
	}
}
