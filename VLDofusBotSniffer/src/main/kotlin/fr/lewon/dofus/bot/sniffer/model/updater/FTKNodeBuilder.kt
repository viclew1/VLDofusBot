package fr.lewon.dofus.bot.sniffer.model.updater

abstract class FTKNodeBuilder(val nodeDescription: FTKNodeDescription) {

    fun getCompleteContent(indentLevel: Int = 0): String {
        val indent = "\t".repeat(indentLevel)
        var text = getLines().joinToString("\n") { "$indent$it" }
        val subNodeBuilders = getSubNodeBuilders()
        if (subNodeBuilders.isNotEmpty()) {
            text += " {\n"
            text += subNodeBuilders.joinToString("") {
                it.getCompleteContent(indentLevel + 1)
            }
            text += "$indent}"
        }
        return text + "\n"
    }

    protected abstract fun getLines(): List<String>

    protected abstract fun getSubNodeBuilders(): List<FTKNodeBuilder>

}