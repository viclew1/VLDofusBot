package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.prism

import fr.lewon.dofus.bot.sniffer.model.types.actor.human.social.alliance.AllianceInformations
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class AlliancePrismInformation : PrismInformation() {

    lateinit var alliance: AllianceInformations

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        alliance = AllianceInformations()
        alliance.deserialize(stream)
    }
}