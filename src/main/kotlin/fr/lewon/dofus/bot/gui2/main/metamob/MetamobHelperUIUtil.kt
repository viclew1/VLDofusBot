package fr.lewon.dofus.bot.gui2.main.metamob

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toPainter
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui2.main.metamob.filter.MonsterFilter
import fr.lewon.dofus.bot.gui2.main.metamob.monsters.MonsterPainterState
import fr.lewon.dofus.bot.util.external.metamob.MetamobMonstersUpdater
import fr.lewon.dofus.bot.util.external.metamob.MetamobRequestProcessor
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster
import java.awt.image.BufferedImage
import java.util.concurrent.locks.ReentrantLock

object MetamobHelperUIUtil {

    private val uiState = mutableStateOf(MetamobHelperUIState())
    private val painterByImageUrl = HashMap<String, MutableState<MonsterPainterState>>()
    private val lock = ReentrantLock()

    fun getValueByFilter(): Map<MonsterFilter, String> {
        return uiState.value.valueByFilter
    }

    fun getMonsters(): List<MetamobMonster> {
        return uiState.value.metamobMonsters
    }

    fun getFilteredMonsters(): List<MetamobMonster> {
        val filters = getValueByFilter()
        return getMonsters().filter { monster ->
            filters.all { it.key.isMonsterValid(it.value, monster) }
        }
    }

    fun refreshMonsters() {
        var monsters: List<MetamobMonster>? = null
        var errorMessage = ""
        if (!MetamobMonstersUpdater.isMetamobConfigured()) {
            errorMessage = "Metamob user not configured, check your settings."
        } else {
            monsters = MetamobRequestProcessor.getAllMonsters()
            if (monsters == null) {
                errorMessage = "Couldn't retrieve metamob monsters, check your settings."
            }
        }
        uiState.value = uiState.value.copy(
            metamobMonsters = monsters ?: emptyList(),
            errorMessage = errorMessage
        )
    }

    fun getErrorMessage(): String {
        return uiState.value.errorMessage
    }

    fun getPainter(monster: MetamobMonster): Painter? {
        return lock.executeSyncOperation {
            getMonsterPainterState(monster).value.painter
        }
    }

    private fun getMonsterPainterState(monster: MetamobMonster): MutableState<MonsterPainterState> {
        return lock.executeSyncOperation {
            painterByImageUrl.computeIfAbsent(monster.imageUrl) { mutableStateOf(MonsterPainterState()) }
        }
    }

    fun loadImagePainter(monster: MetamobMonster) {
        val state = getMonsterPainterState(monster)
        val shouldLoad = !state.value.loaded
        lock.executeSyncOperation {
            if (shouldLoad) {
                state.value = state.value.copy(loaded = true)
            }
        }
        if (shouldLoad) {
            Thread {
                val painter = doLoadImage(monster)?.toPainter()
                lock.executeSyncOperation {
                    state.value = state.value.copy(
                        loaded = true,
                        painter = painter
                    )
                }
            }.start()
        }
    }

    @Synchronized
    private fun doLoadImage(monster: MetamobMonster): BufferedImage? {
        return MetamobRequestProcessor.getImage(monster.imageUrl)
    }

    fun updateFilter(filter: MonsterFilter, newValue: String) {
        uiState.value = uiState.value.copy(
            valueByFilter = uiState.value.valueByFilter.plus(filter to newValue)
        )
    }
}