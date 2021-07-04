package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.taxcollector

import fr.lewon.dofus.bot.sniffer.model.types.actor.human.social.alliance.AllianceInformations
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class TaxCollectorStaticExtendedInformations : TaxCollectorStaticInformations() {

    lateinit var allianceIdentity: AllianceInformations

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        allianceIdentity = AllianceInformations()
        allianceIdentity.deserialize(stream)
    }
}