package fr.lewon.dofus.bot.sniffer.model.updater.nodes.body.fields

enum class VariableType(val flashTypeRegex: Regex, val kotlinType: String, val defaultValue: String) {
    DOUBLE(Regex("Number"), "Double", "0.0"),
    INT(Regex("uint"), "Int", "0"),
    INT2(Regex("int"), "Int", "0"),
    BOOLEAN(Regex("Boolean"), "Boolean", "false"),
    STRING(Regex("String"), "String", "\"\""),
    BYTE_ARRAY(Regex("ByteArray"), "ByteArray", "ByteArray(0)"),
    LIST(Regex("Vector\\.<(.*)>"), "ArrayList<${VariableUtils.INNER_TYPE_TOKEN}>", "ArrayList()"),
    ;

    companion object {

        fun fromFlashType(flashType: String): VariableType? =
            VariableType.entries.firstOrNull { it.flashTypeRegex.matches(flashType) }
    }
}