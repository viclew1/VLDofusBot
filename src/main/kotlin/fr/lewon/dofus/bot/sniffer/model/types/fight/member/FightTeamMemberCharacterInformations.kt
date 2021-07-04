package fr.lewon.dofus.bot.sniffer.model.types.fight.member

import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class FightTeamMemberCharacterInformations : FightTeamMemberInformations() {

    lateinit var name: String
    var level = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        name = stream.readUTF()
        level = stream.readVarShort()
    }
}