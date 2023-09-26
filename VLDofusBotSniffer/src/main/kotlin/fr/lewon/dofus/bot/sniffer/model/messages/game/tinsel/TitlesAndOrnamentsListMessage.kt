package fr.lewon.dofus.bot.sniffer.model.messages.game.tinsel

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TitlesAndOrnamentsListMessage : NetworkMessage() {
	var titles: ArrayList<Int> = ArrayList()
	var ornaments: ArrayList<Int> = ArrayList()
	var activeTitle: Int = 0
	var activeOrnament: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		titles = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			titles.add(item)
		}
		ornaments = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			ornaments.add(item)
		}
		activeTitle = stream.readVarShort().toInt()
		activeOrnament = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 4737
}
