package fr.lewon.dofus.bot.core.model.maps

data class DofusHint(
    val id: Int,
    val outdoor: Boolean,
    val worldMap: DofusWorldMap?,
    val level: Int,
    val gfx: Int,
    val x: Int,
    val y: Int,
    val name: String,
    val map: DofusMap,
    val categoryId: Int
)