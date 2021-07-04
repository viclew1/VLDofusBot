package fr.lewon.dofus.bot.sniffer.model.types.actor.human.social.alliance

import fr.lewon.dofus.bot.sniffer.model.types.actor.human.social.AbstractSocialGroupInfos
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

open class BasicAllianceInformations : AbstractSocialGroupInfos() {

    var allianceId = -1
    lateinit var allianceTag: String

    override fun deserialize(stream: ByteArrayReader) {
        allianceId = stream.readVarInt()
        allianceTag = stream.readUTF()
    }
}