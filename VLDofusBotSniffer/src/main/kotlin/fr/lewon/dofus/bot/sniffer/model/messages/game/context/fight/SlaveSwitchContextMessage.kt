package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic.CharacterCharacteristicsInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.SpellItem
import fr.lewon.dofus.bot.sniffer.model.types.game.shortcut.Shortcut
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SlaveSwitchContextMessage : NetworkMessage() {
	var masterId: Double = 0.0
	var slaveId: Double = 0.0
	var slaveTurn: Int = 0
	var slaveSpells: ArrayList<SpellItem> = ArrayList()
	lateinit var slaveStats: CharacterCharacteristicsInformations
	var shortcuts: ArrayList<Shortcut> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		masterId = stream.readDouble().toDouble()
		slaveId = stream.readDouble().toDouble()
		slaveTurn = stream.readVarShort().toInt()
		slaveSpells = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = SpellItem()
			item.deserialize(stream)
			slaveSpells.add(item)
		}
		slaveStats = CharacterCharacteristicsInformations()
		slaveStats.deserialize(stream)
		shortcuts = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<Shortcut>(stream.readUnsignedShort())
			item.deserialize(stream)
			shortcuts.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 7542
}
