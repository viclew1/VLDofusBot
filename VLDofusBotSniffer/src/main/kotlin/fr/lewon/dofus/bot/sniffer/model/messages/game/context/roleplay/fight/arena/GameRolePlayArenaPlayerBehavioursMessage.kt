package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.fight.arena

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayArenaPlayerBehavioursMessage : NetworkMessage() {
	var flags: ArrayList<String> = ArrayList()
	var sanctions: ArrayList<String> = ArrayList()
	var banDuration: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		flags = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUTF()
			flags.add(item)
		}
		sanctions = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUTF()
			sanctions.add(item)
		}
		banDuration = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 9801
}
