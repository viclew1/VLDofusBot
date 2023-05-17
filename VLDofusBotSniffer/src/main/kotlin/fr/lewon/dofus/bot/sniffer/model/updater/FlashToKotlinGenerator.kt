package fr.lewon.dofus.bot.sniffer.model.updater

import fr.lewon.dofus.bot.sniffer.model.updater.nodes.ClassNodeBuilder
import fr.lewon.dofus.bot.sniffer.model.updater.nodes.PackageNodeBuilder
import fr.lewon.dofus.bot.sniffer.model.updater.nodes.dependency.DependencyNodeBuilder

object FlashToKotlinGenerator {

    fun generateClass(path: String, name: String, fileContent: String): String {
        val fileDescription = FTKNodeDescription(path, name, fileContent)
        val dependencyPart = DependencyNodeBuilder.fromFileDescription(fileDescription)
            .joinToString("") { it.getCompleteContent() }
        return "${PackageNodeBuilder(fileDescription).getCompleteContent()}\n" +
                "$dependencyPart\n" +
                ClassNodeBuilder(fileDescription).getCompleteContent()
    }

}