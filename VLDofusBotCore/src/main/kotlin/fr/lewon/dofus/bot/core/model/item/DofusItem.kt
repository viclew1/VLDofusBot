package fr.lewon.dofus.bot.core.model.item

data class DofusItem(
    val id: Double,
    val name: String,
    val iconId: Int,
    val typeId: Int,
    val effects: List<DofusItemEffect>
)