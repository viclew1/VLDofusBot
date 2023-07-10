package fr.lewon.dofus.bot.core.model.maps


data class DofusMap(
    val subArea: DofusSubArea,
    val worldMap: DofusWorldMap?,
    val id: Double,
    val posX: Int,
    val posY: Int,
    val name: String,
    val outdoor: Boolean,
    val isTransition: Boolean,
    val hasPriorityOnWorldMap: Boolean,
    val capabilities: Int,
    val hasPublicPaddock: Boolean
) {
    val coordinates = DofusCoordinates(posX, posY)
    val canReachHavenBag = capabilities and 8 != 0 && capabilities and 4 != 0
    val canFightMonster = capabilities and 16384 != 0
    val canSpawnMonsters = capabilities and 8192 != 0

    override fun toString(): String = "$coordinates (ID : ${id.toLong()})"
}