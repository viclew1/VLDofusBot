package fr.lewon.dofus.bot.gui.main.auctionhouse

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.core.d2o.managers.item.EffectManager
import fr.lewon.dofus.bot.core.d2p.gfx.D2PItemsGfxAdapter
import fr.lewon.dofus.bot.core.model.item.DofusEffect
import fr.lewon.dofus.bot.core.model.item.DofusItem
import fr.lewon.dofus.bot.core.model.item.DofusItemEffect
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.gui.util.toPainter
import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.BidExchangerObjectInfo
import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects.ObjectEffectInteger
import kotlin.math.abs

@Composable
fun AuctionHouseItemFinderContent() {
    Row(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle().padding(5.dp)) {
        Column(Modifier.width(300.dp).fillMaxHeight()) {
            ItemFiltersContent()
        }
        FilteredItemsGridContent()
    }
}

@Composable
fun ItemFiltersContent() {
    Column(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle()) {
        val item = AuctionHouseItemFinderUIUtil.getUiState().item
        Header(item)
        HorizontalSeparator()
        NativeStatsFilterContent(item)
        HorizontalSeparator()
        AdditionalStatsFilterContent(item)
    }
}

@Composable
private fun Header(item: DofusItem?) {
    Row {
        Column {
            CommonText(
                "Item : ${item?.name ?: "/"}",
                modifier = Modifier.padding(10.dp),
                fontWeight = FontWeight.SemiBold
            )
            CommonText(AuctionHouseItemFinderUIUtil.getLastPriceUpdateTime(), modifier = Modifier.padding(10.dp))
        }
        Spacer(Modifier.fillMaxWidth().weight(1f))
        Box(
            Modifier.size(50.dp).border(BorderStroke(1.dp, Color.LightGray)).align(Alignment.CenterVertically)
                .padding(end = 5.dp)
        ) {
            if (item != null) {
                val gfxId = item.iconId
                val gfxImageData = D2PItemsGfxAdapter.getItemGfxImageData(gfxId.toDouble())
                Image(gfxImageData.toPainter(), "", Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun ColumnScope.NativeStatsFilterContent(item: DofusItem?) {
    val uiState = AuctionHouseItemFinderUIUtil.getUiState()
    val effects = item?.effects?.filter { it.effect.characteristic != null }
        ?.filter { it.effect.operator == "+" || it.effect.operator == "-" }
        ?: emptyList()
    Column(Modifier.fillMaxSize().weight(1f)) {
        val state = rememberScrollState()
        SubTitleText("Native characteristics", modifier = Modifier.padding(start = 10.dp, top = 5.dp))
        if (item != null) {
            Box(Modifier.fillMaxWidth().padding(10.dp)) {
                Column(Modifier.verticalScroll(state).padding(end = 10.dp)) {
                    for (effect in effects) {
                        NativeFilterLine(uiState, item, effect)
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(state),
                )
            }
        }
    }
}

@Composable
private fun NativeFilterLine(
    uiState: AuctionHouseItemFinderUIState,
    item: DofusItem,
    itemEffect: DofusItemEffect,
) {
    val characteristic = itemEffect.effect.characteristic
    if (characteristic != null) {
        Row(Modifier.fillMaxWidth().padding(2.dp)) {
            SimpleTextField(
                uiState.nativeMinValuesByEffect[itemEffect.effect]?.toString() ?: "",
                { AuctionHouseItemFinderUIUtil.updateNativeFilter(itemEffect.effect, it.toIntOrNull()) },
                Modifier.width(40.dp).align(Alignment.CenterVertically)
            )
            Spacer(Modifier.width(10.dp))
            ItemEffectDescriptionText(item, itemEffect)
        }
    }
}

@Composable
private fun ColumnScope.AdditionalStatsFilterContent(item: DofusItem?) {
    val uiState = AuctionHouseItemFinderUIUtil.getUiState()
    Column(Modifier.fillMaxSize().weight(1f)) {
        val state = rememberScrollState()
        SubTitleText("Additional characteristics", modifier = Modifier.padding(start = 10.dp, top = 5.dp))
        if (item != null) {
            Box(Modifier.fillMaxWidth().padding(10.dp)) {
                Column(Modifier.verticalScroll(state).padding(end = 10.dp)) {
                    Row(Modifier.fillMaxWidth().padding(5.dp)) {
                        Row(Modifier.height(30.dp).align(Alignment.CenterVertically)) {
                            ButtonWithTooltip(
                                onClick = { AuctionHouseItemFinderUIUtil.addAdditionalFilter() },
                                title = "",
                                imageVector = Icons.Default.Add,
                                shape = RoundedCornerShape(percent = 5),
                                hoverBackgroundColor = AppColors.GREEN
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        CommonText("Add filter", modifier = Modifier.align(Alignment.CenterVertically))
                    }
                    for ((index, filter) in uiState.additionalFilters.withIndex()) {
                        AdditionalFilterLine(filter, index)
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(state),
                )
            }
        }
    }
}

@Composable
private fun AdditionalFilterLine(filter: AdditionalFilter, index: Int) {
    val characteristic = filter.effect.characteristic
    if (characteristic != null) {
        Row(Modifier.fillMaxWidth().padding(2.dp).height(20.dp)) {
            Row(Modifier.align(Alignment.CenterVertically)) {
                ButtonWithTooltip(
                    onClick = { AuctionHouseItemFinderUIUtil.removeAdditionalFilter(index) },
                    title = "Delete",
                    imageVector = Icons.Default.Close,
                    shape = RoundedCornerShape(percent = 15),
                    hoverBackgroundColor = AppColors.RED,
                )
            }
            Spacer(Modifier.width(10.dp))
            SimpleTextField(
                filter.minValue?.toString() ?: "",
                { AuctionHouseItemFinderUIUtil.updateAdditionalFilter(filter, minValue = it.toIntOrNull()) },
                Modifier.width(40.dp).align(Alignment.CenterVertically)
            )
            Spacer(Modifier.width(10.dp))
            val availableEffects = AuctionHouseItemFinderUIUtil.getAvailableAdditionalEffects()
            ComboBox(
                selectedItem = filter.effect,
                items = availableEffects,
                getItemText = { getEffectDescriptionText(it, 1, 1).trim() },
                onItemSelect = { AuctionHouseItemFinderUIUtil.updateAdditionalFilter(filter, effect = it) },
                maxDropDownHeight = 250.dp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun RowScope.ItemEffectDescriptionText(item: DofusItem, itemEffect: DofusItemEffect) {
    Text(
        buildAnnotatedString {
            val style = getEffectTextStyle(
                item, itemEffect.effect, itemEffect.realMaxValue ?: itemEffect.realMinValue
            )
            buildRangeText(item, itemEffect.effect, style)
            withStyle(SpanStyle()) {
                append(
                    getEffectDescriptionText(
                        itemEffect.effect,
                        itemEffect.realMinValue,
                        itemEffect.realMaxValue
                    )
                )
            }
        },
        fontSize = 13.sp,
        modifier = Modifier.align(Alignment.CenterVertically)
    )
}

@Composable
private fun FilteredItemsGridContent() {
    val uiState = AuctionHouseItemFinderUIUtil.getUiState()
    val item = uiState.item ?: return
    val state = rememberLazyGridState()
    Box(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle().padding(5.dp)) {
        val filters = uiState.nativeMinValuesByEffect.plus(uiState.additionalFilters.associate {
            it.effect to (it.minValue ?: Int.MIN_VALUE)
        })
        val filteredAvailableItems = uiState.availableItems.filter { availableItem ->
            filters.all { filter ->
                val effect = filter.key
                val effectValue = availableItem.effects.filterIsInstance<ObjectEffectInteger>()
                    .firstOrNull { it.actionId == effect.id }
                    ?.let { if (effect.operator == "-") -it.value else it.value } ?: 0
                effectValue >= filter.value
            }
        }.sortedBy { it.prices.firstOrNull() ?: Double.MAX_VALUE }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(240.dp),
            state = state,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            items(filteredAvailableItems) { availableItem ->
                Box(Modifier.padding(2.dp).darkGrayBoxStyle().padding(2.dp)) {
                    AvailableItemCardContent(item, availableItem)
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(state)
        )
    }
}

@Composable
private fun AvailableItemCardContent(item: DofusItem, availableItem: BidExchangerObjectInfo) {
    Column {
        Row(Modifier.fillMaxWidth().padding(5.dp).grayBoxStyle().padding(5.dp)) {
            Image(UiResource.KAMAS.imagePainter, "", modifier = Modifier.size(15.dp).align(Alignment.CenterVertically))
            val priceStr = availableItem.prices.firstOrNull()?.toInt()?.let {
                "%,d".format(it)
            } ?: "?"
            CommonText(" $priceStr", modifier = Modifier.align(Alignment.CenterVertically))
        }
        val allItemEffects = availableItem.effects.map { EffectManager.getEffect(it.actionId) }
        val effectsToDisplay = getSortedEffectsToDisplay(item, allItemEffects)
        for (effect in effectsToDisplay) {
            val itemEffect = availableItem.effects.filterIsInstance<ObjectEffectInteger>()
                .firstOrNull { it.actionId == effect.id }
                ?: ObjectEffectInteger()
            val value = getItemEffectValue(effect, itemEffect)
            if (value != null || AuctionHouseItemFinderUIUtil.VALID_EFFECT_IDS_WITHOUT_VALUE.contains(effect.id)) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 3.dp)) {
                    Text(
                        buildAnnotatedString {
                            val style = getEffectTextStyle(item, effect, value)
                            if (value != null) {
                                withStyle(style) { append(value.toString()) }
                            }
                            withStyle(SpanStyle()) { append(getEffectDescriptionText(effect, value, null)) }
                            if (item.effects.any { it.effect == effect }) {
                                withStyle(SpanStyle(color = Color.Gray)) { append(" [") }
                                buildRangeText(item, effect, SpanStyle(color = Color.Gray))
                                withStyle(SpanStyle(color = Color.Gray)) { append("]") }
                            }
                        },
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
            }
        }
    }
}

private fun getItemEffectValue(effect: DofusEffect, itemEffect: ObjectEffectInteger) = when (effect.operator) {
    "+" -> itemEffect.value
    "-" -> -itemEffect.value
    else -> null
}

private fun getSortedEffectsToDisplay(item: DofusItem, allEffects: List<DofusEffect>): List<DofusEffect> {
    val itemNativeEffects = item.effects.map { it.effect }.toSet()
    val additionalEffects = allEffects.minus(itemNativeEffects)
    return additionalEffects.plus(itemNativeEffects)
}

private fun getEffectDescriptionText(effect: DofusEffect, min: Int?, max: Int?): String {
    val description = effect.description
    val isPlural = min != null && abs(min) > 1 || max != null && abs(max) > 1
    val text = if (description.contains("#")) {
        description.substring(description.lastIndexOf("#") + 2)
    } else description
    return text.replace("{~zs}", "")
        .replace("{~ps}", if (isPlural) "s" else "")
}

private fun getEffectTextStyle(item: DofusItem, effect: DofusEffect, value: Int?): SpanStyle {
    if (value == 0) {
        return SpanStyle(color = Color.Gray)
    }
    val itemEffect = item.effects.firstOrNull { it.effect == effect }
    return if (itemEffect != null) {
        val realMaxValue = itemEffect.realMaxValue
        val realMinValue = itemEffect.realMinValue
        val color = if (value != null && realMinValue != null && value < realMinValue) {
            Color(255, 140, 0)// Orange
        } else if (value != null && value > 0) {
            AppColors.GREEN
        } else if (value != null && value < 0) {
            AppColors.RED
        } else {
            Color.Gray
        }
        if (value != null && realMaxValue != null && value > realMaxValue) {
            SpanStyle(color = color, fontWeight = FontWeight.ExtraBold)
        } else {
            SpanStyle(color = color)
        }
    } else {
        SpanStyle(color = AppColors.primaryLightColor)
    }
}

private fun AnnotatedString.Builder.buildRangeText(item: DofusItem, effect: DofusEffect, style: SpanStyle) {
    val itemEffect = item.effects.firstOrNull { it.effect == effect }
    if (itemEffect != null) {
        val min = itemEffect.realMinValue
        val max = itemEffect.realMaxValue
        if (min != null) {
            withStyle(style) {
                append(min.toString())
            }
            if (max != null) {
                withStyle(SpanStyle(color = Color.DarkGray)) {
                    append(" - ")
                }
            }
        }
        if (max != null) {
            withStyle(style) {
                append(max.toString())
            }
        }
    }
}