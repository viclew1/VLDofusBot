package fr.lewon.dofus.bot.gui.main.auctionhouse

import fr.lewon.dofus.bot.core.model.item.DofusEffect
import fr.lewon.dofus.bot.core.model.item.DofusItem
import fr.lewon.dofus.bot.core.model.item.DofusItemEffect
import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.BidExchangerObjectInfo

data class AuctionHouseItemFinderUIState(
    val item: DofusItem?,
    val nativeItemEffects: List<DofusItemEffect> = emptyList(),
    val updateTimeMillis: Long? = null,
    val refreshing: Boolean = false,
    val nativeMinValuesByEffect: Map<DofusEffect, Int> = emptyMap(),
    val additionalFilters: List<AdditionalFilter> = emptyList(),
    val availableItems: List<BidExchangerObjectInfo> = emptyList(),
)

class AdditionalFilter(
    val effect: DofusEffect,
    val minValue: Int?,
)