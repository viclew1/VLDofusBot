package fr.lewon.dofus.bot.sniffer.model.types.actor.human.options

import fr.lewon.dofus.bot.sniffer.model.types.actor.human.social.alliance.AllianceInformations
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class HumanOptionAlliance : HumanOption() {

    lateinit var allianceInformations: AllianceInformations
    var aggressable = -1

    override fun deserialize(stream: ByteArrayReader) {
        allianceInformations = AllianceInformations()
        allianceInformations.deserialize(stream)
        aggressable = stream.readByte().toInt()
    }
}