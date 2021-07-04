package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.taxcollector

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.types.actor.human.social.guild.GuildInformations
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

open class TaxCollectorStaticInformations : INetworkType {

    var firstNameId = -1
    var lastNameId = -1
    lateinit var guildIdentity: GuildInformations
    var callerId = -1L

    override fun deserialize(stream: ByteArrayReader) {
        firstNameId = stream.readVarShort()
        lastNameId = stream.readVarShort()
        guildIdentity = GuildInformations()
        guildIdentity.deserialize(stream)
        callerId = stream.readVarLong()
    }
}