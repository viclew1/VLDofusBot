package fr.lewon.dofus.bot.sniffer.model.types.actor.human.social.guild

import fr.lewon.dofus.bot.sniffer.model.types.actor.human.social.AbstractSocialGroupInfos
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

open class BasicGuildInformations : AbstractSocialGroupInfos() {

    var guildId = -1
    lateinit var guildName: String
    var guildLevel = -1

    override fun deserialize(stream: ByteArrayReader) {
        guildId = stream.readVarInt()
        guildName = stream.readUTF()
        guildLevel = stream.readByte().toUByte().toInt()
    }
}