package fr.lewon.dofus.bot.sniffer.model.types.game.actions.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameActionMark : NetworkType() {
	var markAuthorId: Double = 0.0
	var markTeamId: Int = 0
	var markSpellId: Int = 0
	var markSpellLevel: Int = 0
	var markId: Int = 0
	var markType: Int = 0
	var markimpactCell: Int = 0
	var cells: ArrayList<GameActionMarkedCell> = ArrayList()
	var active: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		markAuthorId = stream.readDouble().toDouble()
		markTeamId = stream.readUnsignedByte().toInt()
		markSpellId = stream.readInt().toInt()
		markSpellLevel = stream.readUnsignedShort().toInt()
		markId = stream.readUnsignedShort().toInt()
		markType = stream.readUnsignedByte().toInt()
		markimpactCell = stream.readUnsignedShort().toInt()
		cells = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = GameActionMarkedCell()
			item.deserialize(stream)
			cells.add(item)
		}
		active = stream.readBoolean()
	}
}
