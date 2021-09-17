package fr.lewon.dofus.bot.sniffer.model

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

interface INetworkType {

    fun deserialize(stream: ByteArrayReader)

}