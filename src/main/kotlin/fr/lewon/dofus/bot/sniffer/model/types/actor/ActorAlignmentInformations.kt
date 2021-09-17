package fr.lewon.dofus.bot.sniffer.model.types.actor

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

open class ActorAlignmentInformations : INetworkType {

    var alignmentSide = -1
    var alignmentValue = -1
    var alignmentGrade = -1
    var characterPower = -1.0

    override fun deserialize(stream: ByteArrayReader) {
        alignmentSide = stream.readByte().toInt()
        alignmentValue = stream.readByte().toInt()
        alignmentGrade = stream.readByte().toInt()
        characterPower = stream.readDouble()
    }
}