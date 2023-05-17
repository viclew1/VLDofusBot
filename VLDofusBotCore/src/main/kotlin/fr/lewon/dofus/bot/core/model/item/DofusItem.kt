package fr.lewon.dofus.bot.core.model.item

data class DofusItem(
    val id: Double,
    val effects: List<DofusItemEffect>
)