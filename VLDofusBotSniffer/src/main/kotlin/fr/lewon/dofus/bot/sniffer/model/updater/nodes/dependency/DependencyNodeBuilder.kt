package fr.lewon.dofus.bot.sniffer.model.updater.nodes.dependency

import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.updater.*

class DependencyNodeBuilder(
    nodeDescription: FTKNodeDescription,
    private val dependency: DependencyDeclaration
) : FTKNodeBuilder(nodeDescription) {

    companion object {

        private val defaultDependencies = listOf(
            ByteArrayReader::class.java,
            NetworkMessage::class.java,
            NetworkType::class.java,
            ProtocolTypeManager::class.java,
            BooleanByteWrapper::class.java
        ).map { DependencyDeclaration(it.packageName, it.simpleName) }

        fun fromFileDescription(nodeDescription: FTKNodeDescription): List<DependencyNodeBuilder> {
            return Regex("import (.*)\\.(.*?);").findAll(nodeDescription.fileContent)
                .map { DependencyDeclaration(it.destructured.component1(), it.destructured.component2()) }
                .filter(this::isDependencyValid)
                .plus(defaultDependencies)
                .map { DependencyNodeBuilder(nodeDescription, it) }
                .toList()
        }

        private fun isDependencyValid(dependency: DependencyDeclaration): Boolean =
            dependency.path.contains(BASE_MESSAGES_PATH) || dependency.path.contains(BASE_TYPES_PATH)
    }

    override fun getLines(): List<String> {
        val path = dependency.path
            .replace(BASE_MESSAGES_PATH, DEST_MESSAGES_PATH)
            .replace(BASE_TYPES_PATH, DEST_TYPES_PATH)
        return listOf("import $path.${dependency.name}")
    }

    override fun getSubNodeBuilders(): List<FTKNodeBuilder> = emptyList()

}