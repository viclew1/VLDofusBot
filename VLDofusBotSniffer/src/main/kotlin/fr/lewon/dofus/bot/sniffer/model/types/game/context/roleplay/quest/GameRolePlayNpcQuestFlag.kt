package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.quest

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayNpcQuestFlag : NetworkType() {
	var questsToValidId: ArrayList<Int> = ArrayList()
	var questsToStartId: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		questsToValidId = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			questsToValidId.add(item)
		}
		questsToStartId = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			questsToStartId.add(item)
		}
	}
}
