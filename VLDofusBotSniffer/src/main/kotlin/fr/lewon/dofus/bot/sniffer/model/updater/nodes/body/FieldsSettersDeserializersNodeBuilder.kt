package fr.lewon.dofus.bot.sniffer.model.updater.nodes.body

import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.sniffer.model.updater.DESERIALIZE_FUNC_NAME
import fr.lewon.dofus.bot.sniffer.model.updater.FTKNodeBuilder
import fr.lewon.dofus.bot.sniffer.model.updater.FTKNodeDescription
import fr.lewon.dofus.bot.sniffer.model.updater.STREAM_NAME
import fr.lewon.dofus.bot.sniffer.model.updater.nodes.body.fields.VariableDeclaration
import fr.lewon.dofus.bot.sniffer.model.updater.nodes.body.fields.VariableType
import fr.lewon.dofus.bot.sniffer.model.updater.nodes.body.fields.VariableUtils

class FieldsSettersDeserializersNodeBuilder(
    nodeDescription: FTKNodeDescription,
    private val variable: VariableDeclaration
) : FTKNodeBuilder(nodeDescription) {

    companion object {
        fun fromFileDescription(nodeDescription: FTKNodeDescription): List<FieldsSettersDeserializersNodeBuilder> =
            nodeDescription.variables.map { FieldsSettersDeserializersNodeBuilder(nodeDescription, it) }
    }

    override fun getLines(): List<String> = doGetLines(variable, nodeDescription.fileContent)

    private fun doGetLines(variableDeclaration: VariableDeclaration, fileContent: String): List<String> =
        when (variableDeclaration.variableType) {
            VariableType.INT, VariableType.INT2, VariableType.DOUBLE ->
                listOf(getNumberSetter(variableDeclaration.name, variableDeclaration.variableType, fileContent))
            VariableType.BOOLEAN -> getBooleanSetterLines(variableDeclaration, fileContent)
            VariableType.STRING -> listOf(getAssignation(variableDeclaration, "$STREAM_NAME.readUTF()"))
            VariableType.BYTE_ARRAY -> getByteArraySetterLines()
            VariableType.LIST -> getListSetterLines(variableDeclaration, fileContent)
            null -> getObjectSetter(variableDeclaration, fileContent)
        }

    private fun getNumberSetter(variableName: String, variableType: VariableType?, fileContent: String): String =
        getAssignation(variableName, getNumberValue(variableName, variableType, fileContent))

    private fun getNumberValue(variableName: String, variableType: VariableType?, fileContent: String): String {
        val methodCall = Regex("$variableName = input\\.(.*?);")
            .find(fileContent)
            ?.destructured?.component1()
            ?.replaceStreamFuncNames()
            ?.addCast(variableType)
            ?: error("Couldn't find setter for field $variableName (${nodeDescription.name})")
        return "$STREAM_NAME.$methodCall"
    }

    private fun String.replaceStreamFuncNames(): String = replace("readVarUh", "readVar")
        .replace("readShort", "readUnsignedShort")
        .replace("readByte", "readUnsignedByte")
        .replace("readUnsignedInt", "readInt")

    private fun String.addCast(variableType: VariableType?): String = variableType?.kotlinType
        ?.let { "$this.to${variableType.kotlinType}()" }
        ?: this

    private fun getBooleanSetterLines(variableDeclaration: VariableDeclaration, fileContent: String): List<String> {
        val lines = ArrayList<String>()
        if (fileContent.contains("${variableDeclaration.name} = BooleanByteWrapper")) {
            val matchResult = Regex("${variableDeclaration.name} = BooleanByteWrapper\\.getFlag\\((.*?),(.*?)\\);")
                .find(fileContent)
                ?: error("Couldn't find boolean assignation : ${variableDeclaration.name} (${nodeDescription.name})")
            val boxName = matchResult.destructured.component1()
            val boxIndex = matchResult.destructured.component2().toInt()
            if (boxIndex == 0) {
                lines.add("val $boxName = $STREAM_NAME.readByte()")
            }
            lines.add(getAssignation(variableDeclaration, "BooleanByteWrapper.getFlag($boxName, $boxIndex)"))
        } else {
            lines.add(getAssignation(variableDeclaration, "$STREAM_NAME.readBoolean()"))
        }
        return lines
    }

    private fun getObjectSetter(variableDeclaration: VariableDeclaration, fileContent: String): List<String> {
        val objValue = if (fileContent.contains("${variableDeclaration.name} = ProtocolTypeManager")) {
            "${ProtocolTypeManager::class.java.simpleName}.getInstance<${variableDeclaration.kotlinType}>($STREAM_NAME.readUnsignedShort())"
        } else {
            "${variableDeclaration.kotlinType}()"
        }
        return listOf(
            getAssignation(variableDeclaration, objValue),
            *wrapAssignationInConditionIfNeeded("${variableDeclaration.name}.$DESERIALIZE_FUNC_NAME($STREAM_NAME)").toTypedArray()
        )
    }

    private fun wrapAssignationInConditionIfNeeded(assignationLine: String): List<String> {
        val match = Regex(
            "if\\(input\\.([a-zA-Z]+\\(\\)) == (\\d+)\\) +\\{ +" +
                    "this\\.${variable.name} = null; +" +
                    "} +else +\\{ +" +
                    ".*?" +
                    "}", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.UNIX_LINES)
        ).find(
            nodeDescription.fileContent
                .replace("\r", "")
                .replace("\n", " ")
                .replace("\t", " ")
        )
            ?: return listOf(assignationLine)
        val conditionFunc = match.destructured.component1()
            .replaceStreamFuncNames()
            .addCast(variable.variableType)
        val conditionValue = match.destructured.component2()
        return listOf(
            "if ($STREAM_NAME.$conditionFunc != $conditionValue) {",
            "\t$assignationLine",
            "}"
        )
    }

    private fun getByteArraySetterLines(): List<String> {
        val lengthVarName = "${variable.name}Length"
        return listOf(
            "val $lengthVarName = $STREAM_NAME.readVarInt()",
            "${variable.name} += $STREAM_NAME.readNBytes($lengthVarName)"
        )
    }

    private fun getAssignation(variableDeclaration: VariableDeclaration, value: String): String =
        getAssignation(variableDeclaration.name, value)

    private fun getAssignation(variableName: String, value: String): String = "$variableName = $value"

    private fun getListSetterLines(variableDeclaration: VariableDeclaration, fileContent: String): List<String> {
        val variableName = variableDeclaration.name
        val listItemMethodContent = getFuncContent("_${variableName}Func", fileContent)
        val listTypeStr = VariableUtils.getListTypeStr(nodeDescription, variableDeclaration.name)
        val itemName = getListItemName(variableName, listTypeStr, listItemMethodContent)
        val listType = VariableType.fromFlashType(listTypeStr)
        val kotlinType = listType?.kotlinType ?: listTypeStr
        val subVariableDeclaration = VariableDeclaration(itemName, kotlinType, "", listType)
        val itemAssociationLines = doGetLines(subVariableDeclaration, listItemMethodContent).map {
            it.replace(itemName, "item")
        }
        val itemInitLine = itemAssociationLines.firstOrNull()?.let { "\tval $it" }
            ?: error("No init line for variable $variableName (${nodeDescription.name})")
        val additionalItemInitLines = if (itemAssociationLines.size > 1) {
            itemAssociationLines.subList(1, itemAssociationLines.size).map { "\t$it" }
        } else emptyList()
        return listOf(
            getAssignation(variableDeclaration, "ArrayList()"),
            "for (i in 0 until ${getListLengthValue(variableName, fileContent)}) {",
            itemInitLine,
            *additionalItemInitLines.toTypedArray(),
            "\t${variableDeclaration.name}.add(item)",
            "}"
        )
    }

    private fun getListLengthValue(listVariableName: String, fileContent: String): String {
        val listMethodContent = getFuncContent("_${listVariableName}treeFunc", fileContent)
        val lengthVar = Regex("for\\(var i:uint = 0; i < (.*?);").find(listMethodContent)?.destructured?.component1()
            ?: error("Couldn't find list length : $listVariableName (${nodeDescription.name})")
        return lengthVar.toIntOrNull()?.toString()
            ?: getNumberValue("$lengthVar:uint", VariableType.INT, listMethodContent)
    }

    private fun getListItemName(variableName: String, listTypeStr: String, methodContent: String): String {
        val assignedItemName = Regex("$variableName\\.push\\((.*?)\\)").find(methodContent)
            ?: Regex("($variableName\\[.*?])").find(methodContent)
            ?: error("Couldn't find assigned item name : $variableName (${nodeDescription.name})")
        val itemName = assignedItemName.destructured.component1()
        return if (methodContent.contains("$variableName.push(")) {
            "$itemName:$listTypeStr"
        } else {
            itemName.replace("[", "\\[")
        }
    }

    private fun getFuncContent(funcName: String, fileContent: String): String {
        return fileContent.substringAfter("function $funcName")
            .substringBefore("private function")
    }

    override fun getSubNodeBuilders(): List<FTKNodeBuilder> = emptyList()

}