package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.character.restriction.ActorRestrictionsInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HumanInformations : NetworkType() {
	lateinit var restrictions: ActorRestrictionsInformations
	var sex: Boolean = false
	var options: ArrayList<HumanOption> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		restrictions = ActorRestrictionsInformations()
		restrictions.deserialize(stream)
		sex = stream.readBoolean()
		options = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<HumanOption>(stream.readUnsignedShort())
			item.deserialize(stream)
			options.add(item)
		}
	}
}
