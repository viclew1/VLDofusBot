package fr.lewon.dofus.bot.core.model.maps

import fr.lewon.dofus.bot.core.model.entity.DofusMonster

data class DofusSubArea(
    val id: Double,
    val worldMap: DofusWorldMap?,
    val monsters: List<DofusMonster>,
    val mapIds: List<Double>,
    val packId: Int,
    val isConquestVillage: Boolean,
    val customWorldMap: List<Int>,
    val associatedZaapMapId: Double,
    val name: String,
    val area: DofusArea,
    val psiAllowed: Boolean,
    val displayOnWorldMap: Boolean,
    val level: Int,
    val capturable: Boolean,
    val basicAccountAllowed: Boolean
) {
    val label = "${area.name} (${name})"
}