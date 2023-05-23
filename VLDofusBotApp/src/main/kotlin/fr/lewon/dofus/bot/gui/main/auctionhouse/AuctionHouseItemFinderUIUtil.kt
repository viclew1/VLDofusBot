package fr.lewon.dofus.bot.gui.main.auctionhouse

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.d2o.managers.item.EffectManager
import fr.lewon.dofus.bot.core.d2o.managers.item.ItemManager
import fr.lewon.dofus.bot.core.model.item.DofusEffect
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.BidExchangerObjectInfo
import fr.lewon.dofus.bot.util.ids.EffectIds
import fr.lewon.dofus.bot.util.ids.ItemTypeIds
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.locks.ReentrantLock

object AuctionHouseItemFinderUIUtil : ComposeUIUtil() {
    val VALID_EFFECT_IDS_WITHOUT_VALUE = listOf(
        EffectIds.HUNTING_WEAPON_EFFECT_ID,
        EffectIds.PREVENT_SMITHMAGIC_EFFECT_ID
    )
    private lateinit var availableAdditionalEffects: List<DofusEffect>
    private val uiState = mutableStateOf(AuctionHouseItemFinderUIState(null))
    private val lock = ReentrantLock()

    override fun init() {
        availableAdditionalEffects = ItemManager.getItems().asSequence()
            .filter { it.typeId == ItemTypeIds.SMITHMAGIC_RUNE_ITEM_TYPE_ID }
            .mapNotNull { it.effects.firstOrNull() }
            .map { it.effect }
            .distinct()
            .filter { it.operator == "+" || it.operator == "-" }
            .toList()
    }

    fun getLastPriceUpdateTime(): String {
        val lastUpdateSuffix = if (uiState.value.refreshing) {
            "Refreshing ..."
        } else {
            uiState.value.updateTimeMillis?.let {
                SimpleDateFormat("HH:mm:ss").format(Date(it))
            } ?: "No item yet, open one in auction house."
        }
        return "Last shop refresh : $lastUpdateSuffix"
    }

    fun getAvailableAdditionalEffects() = availableAdditionalEffects

    fun getUiState() = uiState.value

    fun updateNativeFilter(effect: DofusEffect, minValue: Int?) {
        lock.executeSyncOperation {
            val uiStateValue = getUiState()
            val newMinValuesByEffectId = uiStateValue.nativeMinValuesByEffect.toMutableMap()
            minValue?.let { newMinValuesByEffectId[effect] = it } ?: newMinValuesByEffectId.remove(effect)
            uiState.value = uiStateValue.copy(nativeMinValuesByEffect = newMinValuesByEffectId)
        }
    }

    fun updateAdditionalFilter(
        filter: AdditionalFilter,
        minValue: Int? = filter.minValue,
        effect: DofusEffect = filter.effect
    ) {
        lock.executeSyncOperation {
            val uiStateValue = getUiState()
            val newAdditionalFilters = uiStateValue.additionalFilters.toMutableList()
            val index = newAdditionalFilters.indexOf(filter)
            newAdditionalFilters[index] = AdditionalFilter(effect, minValue)
            uiState.value = uiStateValue.copy(additionalFilters = newAdditionalFilters)
        }
    }

    fun removeAdditionalFilter(filterIndex: Int) {
        lock.executeSyncOperation {
            val uiStateValue = getUiState()
            val newMinValuesByEffectId = uiStateValue.additionalFilters.toMutableList()
            newMinValuesByEffectId.removeAt(filterIndex)
            uiState.value = uiStateValue.copy(additionalFilters = newMinValuesByEffectId)
        }
    }

    fun addAdditionalFilter() {
        lock.executeSyncOperation {
            val uiStateValue = getUiState()
            val newFilter = AdditionalFilter(availableAdditionalEffects.first(), null)
            val newMinValuesByEffectId = uiStateValue.additionalFilters.plus(newFilter)
            uiState.value = uiStateValue.copy(additionalFilters = newMinValuesByEffectId)
        }
    }

    fun updateItem(objectGID: Int, itemTypeDescriptions: List<BidExchangerObjectInfo>) {
        lock.executeSyncOperation {
            val item = ItemManager.getItem(objectGID.toDouble())
            uiState.value = uiState.value.copy(
                item = item,
                updateTimeMillis = System.currentTimeMillis(),
                refreshing = false,
                nativeMinValuesByEffect = emptyMap(),
                additionalFilters = buildDefaultAdditionalFilters(),
                availableItems = itemTypeDescriptions
            )
        }
    }

    private fun buildDefaultAdditionalFilters() = listOf(
        EffectIds.AP_EFFECT_ID,
        EffectIds.MP_EFFECT_ID,
        EffectIds.RANGE_EFFECT_ID
    ).map { AdditionalFilter(EffectManager.getEffect(it), null) }
}