package fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameActionFightSpellCastMessage : AbstractGameActionFightTargetedAbilityMessage() {
	var spellId: Int = 0
	var spellLevel: Int = 0
	var portalsIds: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		spellId = stream.readVarShort().toInt()
		spellLevel = stream.readUnsignedShort().toInt()
		portalsIds = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUnsignedShort().toInt()
			portalsIds.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 7293
}
