package fr.lewon.dofus.bot.sniffer.model.updater.nodes

import fr.lewon.dofus.bot.sniffer.model.updater.BASE_MESSAGES_PATH
import fr.lewon.dofus.bot.sniffer.model.updater.FTKNodeBuilder
import fr.lewon.dofus.bot.sniffer.model.updater.FTKNodeDescription
import fr.lewon.dofus.bot.sniffer.model.updater.nodes.body.DeserializeFunctionNodeBuilder
import fr.lewon.dofus.bot.sniffer.model.updater.nodes.body.GetIdFunctionNodeBuilder
import fr.lewon.dofus.bot.sniffer.model.updater.nodes.body.VariableNodeBuilder

class ClassNodeBuilder(nodeDescription: FTKNodeDescription) : FTKNodeBuilder(nodeDescription) {

    override fun getLines(): List<String> {
        var parent = Regex("${nodeDescription.name} (extends|implements) ([a-zA-Z]+)")
            .find(nodeDescription.fileContent)?.destructured?.component2()
            ?: error("Couldn't find class parent : ${nodeDescription.name}")
        if (parent == "INetworkType") {
            parent = "NetworkType"
        }
        return listOf("open class ${nodeDescription.name} : $parent()")
    }

    override fun getSubNodeBuilders(): List<FTKNodeBuilder> {
        val subNodeBuilders = mutableListOf(
            *VariableNodeBuilder.fromFileDescription(nodeDescription).toTypedArray(),
            DeserializeFunctionNodeBuilder(nodeDescription),
        )
        if (nodeDescription.path.startsWith(BASE_MESSAGES_PATH)) {
            subNodeBuilders.add(GetIdFunctionNodeBuilder(nodeDescription))
        }
        return subNodeBuilders
    }
}