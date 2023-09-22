package fr.lewon.dofus.bot.model.characters.paths

import java.util.*

data class MapsPath(
    var name: String = "",
    var subPaths: List<SubPath> = emptyList()
)

data class SubPath(
    var pathName: String = "",
    var name: String = "",
    var enabled: Boolean = true,
    var mapIds: List<Double> = emptyList(),
    var id: String = UUID.randomUUID().toString()
) {

    val displayName: String by lazy {
        name.takeIf(String::isNotEmpty) ?: "[Unnamed]"
    }
}