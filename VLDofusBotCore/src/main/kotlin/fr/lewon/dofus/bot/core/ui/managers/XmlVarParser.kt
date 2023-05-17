package fr.lewon.dofus.bot.core.ui.managers

import fr.lewon.dofus.bot.core.ui.xml.containers.PropertiesHolder

object XmlVarParser {

    fun parse(varValue: String, propertiesHolder: PropertiesHolder): String {
        var trimmedValue = varValue.trim().replace(" ", "")
            .replace("'", "")
        while (trimmedValue.contains("(")) {
            val end = trimmedValue.indexOf(')')
            val start = trimmedValue.substring(0, end).lastIndexOf('(')
            val subStr = trimmedValue.substring(start + 1, end)
            val parenthesisValue = parseWithoutParenthesis(subStr, propertiesHolder)
            trimmedValue = trimmedValue.replaceRange(start, end + 1, parenthesisValue)
        }
        return parseWithoutParenthesis(trimmedValue, propertiesHolder)
    }

    private fun parseWithoutParenthesis(
        blockStr: String,
        propertiesHolder: PropertiesHolder
    ): String {
        if (blockStr.contains("+")) {
            val blocks = blockStr.split("+", limit = 2)
            val firstBlockParsed = parseWithoutParenthesis(blocks[0], propertiesHolder)
            val secondBlockParsed = parseWithoutParenthesis(blocks[1], propertiesHolder)
            return "$firstBlockParsed$secondBlockParsed"
        }
        if (blockStr.contains("==")) {
            val blocks = blockStr.split("==", limit = 2)
            val firstBlockParsed = parseWithoutParenthesis(blocks[0], propertiesHolder)
            val secondBlockParsed = parseWithoutParenthesis(blocks[1], propertiesHolder)
            return (firstBlockParsed == secondBlockParsed).toString()
        }
        if (blockStr.contains("!=")) {
            val blocks = blockStr.split("!=", limit = 2)
            val firstBlockParsed = parseWithoutParenthesis(blocks[0], propertiesHolder)
            val secondBlockParsed = parseWithoutParenthesis(blocks[1], propertiesHolder)
            return (firstBlockParsed != secondBlockParsed).toString()
        }
        if (blockStr.contains("?")) {
            val blocks = blockStr.split("?", limit = 2)
            val firstBlockParsed = parseWithoutParenthesis(blocks[0], propertiesHolder)
            val secondBlockSplit = blocks[1].split(":", limit = 2)
            return if (firstBlockParsed == "true") {
                return parseWithoutParenthesis(secondBlockSplit[0], propertiesHolder)
            } else parseWithoutParenthesis(secondBlockSplit[1], propertiesHolder)
        }
        if (blockStr.contains("-")) {
            val blocks = blockStr.split("-", limit = 2)
            if (blocks[0].isNotBlank()) {
                val firstBlockParsed = parseWithoutParenthesis(blocks[0], propertiesHolder)
                val secondBlockParsed = parseWithoutParenthesis(blocks[1], propertiesHolder)
                return (firstBlockParsed.toInt() - secondBlockParsed.toInt()).toString()
            }
        }
        return propertiesHolder.parseValues(blockStr.trim())
    }

}