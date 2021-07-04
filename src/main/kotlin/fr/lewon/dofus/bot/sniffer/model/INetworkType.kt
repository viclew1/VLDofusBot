package fr.lewon.dofus.bot.sniffer.model

import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

interface INetworkType {

    fun deserialize(stream: ByteArrayReader)

}