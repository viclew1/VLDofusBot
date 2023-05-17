package fr.lewon.dofus.bot.sniffer.model.updater.nodes.body.fields

import fr.lewon.dofus.bot.sniffer.model.updater.FTKNodeDescription

object VariableUtils {

    const val INNER_TYPE_TOKEN = "__INNER_TYPE__"

    private fun getKotlinType(flashType: String): String {
        val variableType = VariableType.fromFlashType(flashType)
            ?: return flashType
        if (variableType.kotlinType.contains(INNER_TYPE_TOKEN)) {
            val innerFlashType = variableType.flashTypeRegex.find(flashType)?.destructured?.component1()
                ?: error("Couldn't find match for inner type in regex - ${variableType.name}")
            return variableType.kotlinType.replace(INNER_TYPE_TOKEN, getKotlinType(innerFlashType))
        }
        return variableType.kotlinType
    }

    fun getVariables(fileContent: String): List<VariableDeclaration> {
        val reversedFileContentLines = fileContent.split("\n").reversed()
        return Regex("public var ([a-zA-Z\\d]+):([^ =;]*)").findAll(fileContent).map {
            val variableName = it.destructured.component1()
            val flashType = it.destructured.component2()
            val variableType = VariableType.fromFlashType(flashType)
            val kotlinType = getKotlinType(flashType)
            val defaultValue = variableType?.defaultValue ?: "$flashType()"
            VariableDeclaration(variableName, kotlinType, defaultValue, variableType)
        }.sortedByDescending { variable ->
            reversedFileContentLines.indexOfFirst { it.contains(Regex("this\\.${variable.name}[^a-zA-Z\\d]")) }
        }.toList()
    }

    fun getListTypeStr(nodeDeclaration: FTKNodeDescription, varName: String): String =
        Regex("public var $varName:Vector\\.<(.*?)>;").find(nodeDeclaration.fileContent)
            ?.destructured?.component1()
            ?: error("Couldn't find list var : $varName (${nodeDeclaration.name})")

}