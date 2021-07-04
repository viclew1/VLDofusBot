package fr.lewon.dofus.bot.sniffer.model.types.actor.human.options

import fr.lewon.dofus.bot.sniffer.model.types.actor.human.social.guild.GuildInformations
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class HumanOptionGuild : HumanOption() {

    lateinit var guildInformations: GuildInformations

    override fun deserialize(stream: ByteArrayReader) {
        guildInformations = GuildInformations()
        guildInformations.deserialize(stream)
    }
}