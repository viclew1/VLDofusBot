package fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.GameFightEffectTriggerCount
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameActionUpdateEffectTriggerCountMessage : NetworkMessage() {
	var targetIds: ArrayList<GameFightEffectTriggerCount> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		targetIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = GameFightEffectTriggerCount()
			item.deserialize(stream)
			targetIds.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 3346
}
