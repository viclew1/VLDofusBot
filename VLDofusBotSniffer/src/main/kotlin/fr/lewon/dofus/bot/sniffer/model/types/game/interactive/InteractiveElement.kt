package fr.lewon.dofus.bot.sniffer.model.types.game.interactive

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class InteractiveElement : NetworkType() {
	var elementId: Int = 0
	var elementTypeId: Int = 0
	var enabledSkills: ArrayList<InteractiveElementSkill> = ArrayList()
	var disabledSkills: ArrayList<InteractiveElementSkill> = ArrayList()
	var onCurrentMap: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		elementId = stream.readInt().toInt()
		elementTypeId = stream.readInt().toInt()
		enabledSkills = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<InteractiveElementSkill>(stream.readUnsignedShort())
			item.deserialize(stream)
			enabledSkills.add(item)
		}
		disabledSkills = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<InteractiveElementSkill>(stream.readUnsignedShort())
			item.deserialize(stream)
			disabledSkills.add(item)
		}
		onCurrentMap = stream.readBoolean()
	}
}
