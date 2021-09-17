package fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.named

import fr.lewon.dofus.bot.sniffer.model.types.actor.ActorAlignmentInformations
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameFightCharacterInformations : GameFightFighterNamedInformations() {

    var level = 0
    var alignmentInfos = ActorAlignmentInformations()
    var breed = 0
    var sex = false

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        level = stream.readVarShort()
        alignmentInfos.deserialize(stream)
        breed = stream.readByte().toInt()
        sex = stream.readBoolean()
    }
}