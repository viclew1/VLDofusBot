package fr.lewon.dofus.bot.sniffer.model.messages.game.shortcut

import fr.lewon.dofus.bot.sniffer.model.types.game.shortcut.Shortcut
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ShortcutBarContentMessage : NetworkMessage() {
	var barType: Int = 0
	var shortcuts: ArrayList<Shortcut> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		barType = stream.readUnsignedByte().toInt()
		shortcuts = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<Shortcut>(stream.readUnsignedShort())
			item.deserialize(stream)
			shortcuts.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 7898
}
