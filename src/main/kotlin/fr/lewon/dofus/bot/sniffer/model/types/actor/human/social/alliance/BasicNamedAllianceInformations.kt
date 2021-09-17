package fr.lewon.dofus.bot.sniffer.model.types.actor.human.social.alliance

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

open class BasicNamedAllianceInformations : BasicAllianceInformations() {

    lateinit var allianceName: String

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        allianceName = stream.readUTF()
    }
}