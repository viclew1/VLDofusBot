package fr.lewon.dofus.bot.core.model.maps

data class DofusWorldMap(
    val id: Int,
    val name: String,
    val visibleOnMap: Boolean,
    val viewableEverywhere: Boolean
)