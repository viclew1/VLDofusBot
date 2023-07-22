package fr.lewon.dofus.bot.core.model.maps

data class DofusArea(
    val id: Double,
    val name: String,
    val superAreaId: Int,
    val hasWorldMap: Boolean,
)