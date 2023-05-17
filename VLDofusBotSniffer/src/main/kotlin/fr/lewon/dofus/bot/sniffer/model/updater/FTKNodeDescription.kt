package fr.lewon.dofus.bot.sniffer.model.updater

import fr.lewon.dofus.bot.sniffer.model.updater.nodes.body.fields.VariableDeclaration
import fr.lewon.dofus.bot.sniffer.model.updater.nodes.body.fields.VariableUtils

data class FTKNodeDescription(val path: String, val name: String, val fileContent: String) {
    val variables: List<VariableDeclaration> by lazy {
        VariableUtils.getVariables(fileContent)
    }
}