package fr.lewon.dofus.bot.sniffer.model.types.actor

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class ActorExtendedAlignmentInformations : ActorAlignmentInformations() {

    var honor = 0
    var honorGradeFloor = 0
    var honorNextGradeFloor = 0
    var aggressable = 0

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        honor = stream.readVarShort()
        honorGradeFloor = stream.readVarShort()
        honorNextGradeFloor = stream.readVarShort()
        aggressable = stream.readByte().toInt()
    }

}