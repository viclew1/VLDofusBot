package fr.lewon.dofus.bot.sniffer.model.types.actor.human.social.alliance

import fr.lewon.dofus.bot.sniffer.model.types.actor.human.social.GuildEmblem
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class AllianceInformations : BasicNamedAllianceInformations() {

    lateinit var allianceEmblem: GuildEmblem

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        allianceEmblem = GuildEmblem()
        allianceEmblem.deserialize(stream)
    }
}