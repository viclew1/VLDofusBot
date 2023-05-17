package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.look.IndexedEntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HumanOptionFollowers : HumanOption() {
	var followingCharactersLook: ArrayList<IndexedEntityLook> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		followingCharactersLook = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = IndexedEntityLook()
			item.deserialize(stream)
			followingCharactersLook.add(item)
		}
	}
}
