package fr.lewon.dofus.bot.core.model.item

import fr.lewon.dofus.bot.core.d2p.gfx.D2PItemsGfxAdapter

data class DofusItem(
    val id: Double,
    val name: String,
    val iconId: Int,
    val typeId: Int,
    val effects: List<DofusItemEffect>,
    val level: Int,
    val realWeight: Int
) {
    val cachedIcon by lazy {
        D2PItemsGfxAdapter.getItemGfxImageData(iconId.toDouble())
    }
}