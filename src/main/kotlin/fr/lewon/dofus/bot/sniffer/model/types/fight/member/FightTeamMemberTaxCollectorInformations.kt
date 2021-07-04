package fr.lewon.dofus.bot.sniffer.model.types.fight.member

import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class FightTeamMemberTaxCollectorInformations : FightTeamMemberInformations() {

    var firstNameId = -1
    var lastNameId = -1
    var level = -1
    var guildId = -1
    var uid = -1.0

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        firstNameId = stream.readVarShort()
        lastNameId = stream.readVarShort()
        level = stream.readByte().toUByte().toInt()
        guildId = stream.readVarInt()
        uid = stream.readDouble()
    }
}