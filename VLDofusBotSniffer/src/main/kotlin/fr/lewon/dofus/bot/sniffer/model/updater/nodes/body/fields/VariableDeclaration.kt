package fr.lewon.dofus.bot.sniffer.model.updater.nodes.body.fields

data class VariableDeclaration(
    val name: String,
    val kotlinType: String,
    val defaultValue: String,
    val variableType: VariableType?
)