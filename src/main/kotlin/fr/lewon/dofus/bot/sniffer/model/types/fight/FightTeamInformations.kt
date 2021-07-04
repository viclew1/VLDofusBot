package fr.lewon.dofus.bot.sniffer.model.types.fight

import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.fight.member.FightTeamMemberInformations
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class FightTeamInformations : AbstractFightTeamInformations() {

    var teamMembers = ArrayList<FightTeamMemberInformations>()

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        for (i in 0 until stream.readShort()) {
            val member = TypeManager.getInstance<FightTeamMemberInformations>(stream.readShort())
            member.deserialize(stream)
            teamMembers.add(member)
        }
    }
}