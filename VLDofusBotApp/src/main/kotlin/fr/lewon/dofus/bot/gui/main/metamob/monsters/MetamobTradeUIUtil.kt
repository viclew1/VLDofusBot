package fr.lewon.dofus.bot.gui.main.metamob.monsters

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster
import java.util.concurrent.locks.ReentrantLock

object MetamobTradeUIUtil : ComposeUIUtil() {

    private val lock = ReentrantLock()
    private val uiState = mutableStateOf(MetamobTradeUiState())

    fun getUiStateValue() = uiState.value

    fun setTradeOpened(tradeOpened: Boolean) = lock.executeSyncOperation {
        uiState.value = uiState.value.copy(tradeOpened = tradeOpened)
    }

    fun clearTrade() = lock.executeSyncOperation {
        updateTrade(emptyList(), emptyList())
    }

    fun updateTrade(playerMonsters: List<MetamobMonster>, otherGuyMonsters: List<MetamobMonster>) =
        lock.executeSyncOperation {
            uiState.value = uiState.value.copy(
                playerTradeMonsters = playerMonsters,
                otherGuyTradeMonsters = otherGuyMonsters
            )
        }

}