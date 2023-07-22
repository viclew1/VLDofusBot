package fr.lewon.dofus.bot.gui.main.exploration.seenmonsters

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import java.util.concurrent.locks.ReentrantLock

object SeenMonstersUiUtil : ComposeUIUtil() {

    private val lock = ReentrantLock()
    private val uiState = mutableStateOf(SeenMonstersUiState())

    fun getUiStateValue() = lock.executeSyncOperation {
        uiState.value
    }

    fun updateSeenMonsters(map: DofusMap, monsters: List<DofusMonster>) {
        val seenMonsters = monsters.map { monster ->
            val type = when {
                monster.isMiniBoss -> SeenMonsterType.Archmonster
                monster.isQuestMonster -> SeenMonsterType.QuestMonster
                else -> SeenMonsterType.Monster
            }
            SeenMonster(System.currentTimeMillis(), map, monster, type)
        }
        lock.executeSyncOperation {
            val uiStateValue = getUiStateValue()
            uiState.value = uiStateValue.copy(
                seenMonstersByMap = uiStateValue.seenMonstersByMap.plus(map to seenMonsters)
            )
        }
    }

    fun removeSeenMonster(seenMonster: SeenMonster) = lock.executeSyncOperation {
        val uiStateValue = getUiStateValue()
        val seenMonstersOnMap = uiStateValue.seenMonstersByMap[seenMonster.map] ?: emptyList()
        uiState.value = uiStateValue.copy(
            seenMonstersByMap = uiStateValue.seenMonstersByMap.plus(
                seenMonster.map to seenMonstersOnMap.minus(seenMonster)
            )
        )
    }

}