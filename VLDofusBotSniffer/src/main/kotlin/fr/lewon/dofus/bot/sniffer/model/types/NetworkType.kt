package fr.lewon.dofus.bot.sniffer.model.types

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

abstract class NetworkType {

    open fun deserialize(stream: ByteArrayReader) {}

}