package fr.lewon.dofus.bot.sniffer.model.updater.nodes.body

import fr.lewon.dofus.bot.sniffer.model.updater.FTKNodeBuilder
import fr.lewon.dofus.bot.sniffer.model.updater.FTKNodeDescription
import fr.lewon.dofus.bot.sniffer.model.updater.nodes.body.fields.VariableDeclaration

class VariableNodeBuilder(
    nodeDescription: FTKNodeDescription,
    private val variableDeclaration: VariableDeclaration
) : FTKNodeBuilder(nodeDescription) {

    companion object {
        fun fromFileDescription(nodeDescription: FTKNodeDescription): List<VariableNodeBuilder> =
            nodeDescription.variables.map { VariableNodeBuilder(nodeDescription, it) }
    }

    override fun getLines(): List<String> {
        val declaration = if (variableDeclaration.variableType == null) {
            "lateinit var ${variableDeclaration.name}: ${variableDeclaration.kotlinType}"
        } else {
            "var ${variableDeclaration.name}: ${variableDeclaration.kotlinType} = ${variableDeclaration.defaultValue}"
        }
        return listOf(declaration)
    }

    override fun getSubNodeBuilders(): List<FTKNodeBuilder> = emptyList()

}