package fr.lewon.dofus.bot.gui.main.metamob

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toPainter
import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.d2p.gfx.D2PMonstersGfxAdapter
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.gui.main.metamob.filter.MonsterFilter
import fr.lewon.dofus.bot.gui.main.metamob.monsters.MetamobMonsterPainter
import fr.lewon.dofus.bot.gui.util.getBufferedImage
import fr.lewon.dofus.bot.gui.util.trimImage
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges.ExchangeTypesItemsExchangerDescriptionForUserMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects.ObjectEffectDice
import fr.lewon.dofus.bot.util.external.metamob.MetamobMonstersHelper
import fr.lewon.dofus.bot.util.external.metamob.MetamobRequestProcessor
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster
import fr.lewon.dofus.bot.util.ids.EffectIds
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.locks.ReentrantLock

object MetamobHelperUIUtil : ComposeUIUtil() {

    val uiState = mutableStateOf(MetamobHelperUIState())
    val refreshingMonsters = mutableStateOf(false)
    private val loadedImageUrls = ArrayList<String>()
    private val painterByImageUrl = HashMap<String, MetamobMonsterPainter>()
    private val lock = ReentrantLock()

    fun getFilteredMonsters(): List<MetamobMonster> {
        val filters = uiState.value.valueByFilter
        return uiState.value.metamobMonsters.filter { monster ->
            filters.all { it.key.isMonsterValid(it.value, monster) }
        }
    }

    @Synchronized
    fun refreshMonsters() {
        refreshingMonsters.value = true
        var monsters: List<MetamobMonster>? = null
        var errorMessage = ""
        try {
            if (!MetamobMonstersHelper.isMetamobConfigured()) {
                errorMessage = "Metamob user not configured, check your settings."
            } else {
                monsters = MetamobRequestProcessor.getAllMonsters()
                if (monsters == null) {
                    errorMessage = "Couldn't retrieve metamob monsters, check your settings."
                }
            }
        } finally {
            uiState.value = uiState.value.copy(
                metamobMonsters = monsters ?: emptyList(),
                errorMessage = errorMessage,
            )
            refreshingMonsters.value = false
        }
    }

    fun getPrice(monster: MetamobMonster): Long? = uiState.value.priceByArchmonsterId[monster.id]

    fun updateArchmonsterPrices(objectBidsMessage: ExchangeTypesItemsExchangerDescriptionForUserMessage) {
        if (!uiState.value.refreshingPrices) {
            uiState.value = uiState.value.copy(refreshingPrices = true)
            val allMetamobMonsters = MetamobRequestProcessor.getAllMonsters()
            if (allMetamobMonsters == null) {
                uiState.value = uiState.value.copy(refreshingPrices = false)
            } else {
                val priceByMonsterId = getPriceByMonsterId(objectBidsMessage)
                val priceByMetamobArchmonsterId =
                    priceByMonsterId.mapKeys { MonsterManager.getMonster(it.key.toDouble()) }
                        .mapKeys { MetamobMonstersHelper.getMetamobMonster(it.key, allMetamobMonsters) }
                        .mapNotNull { e -> e.key?.let { it.id to e.value } }
                        .toMap()
                uiState.value = uiState.value.copy(
                    refreshingPrices = false,
                    priceByArchmonsterId = priceByMetamobArchmonsterId,
                    lastPriceRefreshTimeMillis = System.currentTimeMillis()
                )
            }
        }
    }

    private fun getPriceByMonsterId(objectBidsMessage: ExchangeTypesItemsExchangerDescriptionForUserMessage): HashMap<Int, Long> {
        val priceByMonsterId = HashMap<Int, Long>()
        for (itemTypeDescription in objectBidsMessage.itemTypeDescriptions) {
            val price = itemTypeDescription.prices.filter { it > 0 }.min().toLong()
            val summonedMetamobMonsters = itemTypeDescription.effects
                .filter { it.actionId == EffectIds.MONSTER_STONE_EFFECT_EFFECT_ID }
                .filterIsInstance(ObjectEffectDice::class.java)
            for (summonedMetamobMonster in summonedMetamobMonsters) {
                val currentPrice = priceByMonsterId[summonedMetamobMonster.diceConst] ?: Long.MAX_VALUE
                priceByMonsterId[summonedMetamobMonster.diceConst] = minOf(price, currentPrice)
            }
        }
        return priceByMonsterId
    }

    fun getMonsterPainter(monster: MetamobMonster) = lock.executeSyncOperation {
        painterByImageUrl[monster.imageUrl]
    }

    fun isLoaded(imageUrl: String) = lock.executeSyncOperation {
        loadedImageUrls.contains(imageUrl)
    }

    fun loadImagePainter(monster: MetamobMonster) = lock.executeSyncOperation {
        loadedImageUrls.add(monster.imageUrl)
        Thread {
            val painter = doLoadImage(monster)
            lock.executeSyncOperation {
                painterByImageUrl[monster.imageUrl] = MetamobMonsterPainter(painter)
            }
        }.start()
    }

    @Synchronized
    private fun doLoadImage(metamobMonster: MetamobMonster): Painter? {
        val monster = MetamobMonstersHelper.getDofusMonster(metamobMonster)
            ?: return null
        val iconData = D2PMonstersGfxAdapter.getMonsterImageData(monster.id)
        return iconData.getBufferedImage().trimImage().toPainter()
    }

    fun updateFilter(filter: MonsterFilter, newValue: String) {
        uiState.value = uiState.value.copy(
            valueByFilter = uiState.value.valueByFilter.plus(filter to newValue)
        )
    }

    fun getLastPriceUpdateTime(): String {
        val lastUpdateSuffix = if (uiState.value.refreshingPrices) {
            "Ongoing ..."
        } else {
            val lastPriceRefreshTimeMillis = uiState.value.lastPriceRefreshTimeMillis
                ?: return "No prices yet, open the archmonsters soul stone offers in the auction house."
            SimpleDateFormat("HH:mm:ss").format(Date(lastPriceRefreshTimeMillis))
        }
        return "Last archmonsters prices update : $lastUpdateSuffix"
    }
}