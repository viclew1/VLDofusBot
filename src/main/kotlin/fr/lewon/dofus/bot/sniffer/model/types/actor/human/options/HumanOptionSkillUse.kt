package fr.lewon.dofus.bot.sniffer.model.types.actor.human.options

import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class HumanOptionSkillUse : HumanOption() {

    var elementId = -1
    var skillId = -1
    var skillEndTime = -1.0

    override fun deserialize(stream: ByteArrayReader) {
        elementId = stream.readVarInt()
        skillId = stream.readVarShort()
        skillEndTime = stream.readDouble()
    }
}