package fr.lewon.dofus.bot.sniffer.model.updater.nodes.body

import fr.lewon.dofus.bot.sniffer.model.updater.FTKNodeBuilder
import fr.lewon.dofus.bot.sniffer.model.updater.FTKNodeDescription

class GetIdFunctionNodeBuilder(nodeDescription: FTKNodeDescription) : FTKNodeBuilder(nodeDescription) {

    override fun getLines(): List<String> {
        val messageId = Regex("public static const protocolId:uint = (\\d+);").find(nodeDescription.fileContent)
            ?.destructured?.component1()
            ?: error("Couldn't find message ID : ${nodeDescription.name}")
        return listOf("override fun getNetworkMessageId(): Int = $messageId")
    }

    override fun getSubNodeBuilders(): List<FTKNodeBuilder> = emptyList()
}