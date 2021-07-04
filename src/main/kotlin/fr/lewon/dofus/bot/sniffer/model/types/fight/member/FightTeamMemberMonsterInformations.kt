package fr.lewon.dofus.bot.sniffer.model.types.fight.member

import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class FightTeamMemberMonsterInformations : FightTeamMemberInformations() {

    var monsterId = -1
    var grade = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        monsterId = stream.readInt()
        grade = stream.readByte().toInt()
    }
}