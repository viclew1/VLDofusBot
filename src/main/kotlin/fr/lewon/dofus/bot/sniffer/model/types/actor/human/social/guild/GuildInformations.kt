package fr.lewon.dofus.bot.sniffer.model.types.actor.human.social.guild

import fr.lewon.dofus.bot.sniffer.model.types.actor.human.social.GuildEmblem
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GuildInformations : BasicGuildInformations() {

    lateinit var guildEmblem: GuildEmblem

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        guildEmblem = GuildEmblem()
        guildEmblem.deserialize(stream)
    }
}