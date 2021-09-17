package fr.lewon.dofus.bot.sniffer.model.types.fight.member

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class FightTeamMemberEntityInformation : FightTeamMemberInformations() {

    var entityModelId = -1
    var level = -1
    var masterId = -1.0

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        entityModelId = stream.readByte().toInt()
        level = stream.readVarShort()
        masterId = stream.readDouble()
    }
}