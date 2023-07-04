package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.breach

import fr.lewon.dofus.bot.sniffer.model.types.game.character.CharacterMinimalInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects.ObjectEffectInteger
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BreachStateMessage : NetworkMessage() {
	lateinit var owner: CharacterMinimalInformations
	var bonuses: ArrayList<ObjectEffectInteger> = ArrayList()
	var bugdet: Int = 0
	var saved: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		owner = CharacterMinimalInformations()
		owner.deserialize(stream)
		bonuses = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ObjectEffectInteger()
			item.deserialize(stream)
			bonuses.add(item)
		}
		bugdet = stream.readVarInt().toInt()
		saved = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 7262
}
