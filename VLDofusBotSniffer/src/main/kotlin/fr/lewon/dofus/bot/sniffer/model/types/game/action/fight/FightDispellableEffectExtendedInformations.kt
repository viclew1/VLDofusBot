package fr.lewon.dofus.bot.sniffer.model.types.game.action.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.actions.fight.AbstractFightDispellableEffect
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FightDispellableEffectExtendedInformations : NetworkType() {
	var actionId: Int = 0
	var sourceId: Double = 0.0
	lateinit var effect: AbstractFightDispellableEffect
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		actionId = stream.readVarShort().toInt()
		sourceId = stream.readDouble().toDouble()
		effect = ProtocolTypeManager.getInstance<AbstractFightDispellableEffect>(stream.readUnsignedShort())
		effect.deserialize(stream)
	}
}
