package fr.lewon.dofus.bot.sniffer.model.updater.nodes

import fr.lewon.dofus.bot.sniffer.model.updater.*

class PackageNodeBuilder(nodeDescription: FTKNodeDescription) : FTKNodeBuilder(nodeDescription) {

    override fun getLines(): List<String> {
        val packageStr = nodeDescription.path
            .replace(BASE_MESSAGES_PATH, DEST_MESSAGES_PATH)
            .replace(BASE_TYPES_PATH, DEST_TYPES_PATH)
        return listOf("package $packageStr")
    }

    override fun getSubNodeBuilders(): List<FTKNodeBuilder> = emptyList()
}