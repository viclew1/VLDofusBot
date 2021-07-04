package fr.lewon.dofus.bot.sniffer.model.types.element

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

open class InteractiveElement : INetworkType {

    var elementId = -1
    var elementTypeId = -1
    var enabledSkills = ArrayList<InteractiveElementSkill>()
    var disabledSkills = ArrayList<InteractiveElementSkill>()
    var onCurrentMap = false

    override fun deserialize(stream: ByteArrayReader) {
        elementId = stream.readInt()
        elementTypeId = stream.readInt()
        for (i in 0 until stream.readShort()) {
            val elementSkill = TypeManager.getInstance<InteractiveElementSkill>(stream.readShort())
            elementSkill.deserialize(stream)
            enabledSkills.add(elementSkill)
        }
        for (i in 0 until stream.readShort()) {
            val elementSkill = TypeManager.getInstance<InteractiveElementSkill>(stream.readShort())
            elementSkill.deserialize(stream)
            disabledSkills.add(elementSkill)
        }
        onCurrentMap = stream.readBoolean()
    }

}