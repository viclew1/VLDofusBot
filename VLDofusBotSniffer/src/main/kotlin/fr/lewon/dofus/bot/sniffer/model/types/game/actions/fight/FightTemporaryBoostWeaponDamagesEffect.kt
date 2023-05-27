package fr.lewon.dofus.bot.sniffer.model.types.game.actions.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FightTemporaryBoostWeaponDamagesEffect : FightTemporaryBoostEffect() {
	var weaponTypeId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		weaponTypeId = stream.readUnsignedShort().toInt()
	}
}
