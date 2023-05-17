package fr.lewon.dofus.bot.sniffer.model.types.game.context

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GameRolePlayActorInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayTaxCollectorInformations : GameRolePlayActorInformations() {
	lateinit var identification: TaxCollectorStaticInformations
	var taxCollectorAttack: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		identification = ProtocolTypeManager.getInstance<TaxCollectorStaticInformations>(stream.readUnsignedShort())
		identification.deserialize(stream)
		taxCollectorAttack = stream.readInt().toInt()
	}
}
