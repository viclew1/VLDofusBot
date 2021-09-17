package fr.lewon.dofus.bot.sniffer.model.types.actor.human.options

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class HumanOptionTitle : HumanOption() {

    var titleId = -1
    lateinit var titleParam: String

    override fun deserialize(stream: ByteArrayReader) {
        titleId = stream.readVarShort()
        titleParam = stream.readUTF()
    }
}