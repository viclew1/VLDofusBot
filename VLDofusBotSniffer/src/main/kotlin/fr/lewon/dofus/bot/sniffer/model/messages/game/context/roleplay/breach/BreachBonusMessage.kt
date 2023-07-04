package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.breach

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects.ObjectEffectInteger
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BreachBonusMessage : NetworkMessage() {
	lateinit var bonus: ObjectEffectInteger
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		bonus = ObjectEffectInteger()
		bonus.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 4966
}
