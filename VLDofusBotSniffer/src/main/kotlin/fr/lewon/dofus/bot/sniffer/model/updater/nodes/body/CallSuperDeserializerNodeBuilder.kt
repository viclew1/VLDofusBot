package fr.lewon.dofus.bot.sniffer.model.updater.nodes.body

import fr.lewon.dofus.bot.sniffer.model.updater.DESERIALIZE_FUNC_NAME
import fr.lewon.dofus.bot.sniffer.model.updater.FTKNodeBuilder
import fr.lewon.dofus.bot.sniffer.model.updater.FTKNodeDescription
import fr.lewon.dofus.bot.sniffer.model.updater.STREAM_NAME

class CallSuperDeserializerNodeBuilder(nodeDescription: FTKNodeDescription) : FTKNodeBuilder(nodeDescription) {

    override fun getLines(): List<String> = listOf("super.$DESERIALIZE_FUNC_NAME($STREAM_NAME)")

    override fun getSubNodeBuilders(): List<FTKNodeBuilder> = emptyList()

}