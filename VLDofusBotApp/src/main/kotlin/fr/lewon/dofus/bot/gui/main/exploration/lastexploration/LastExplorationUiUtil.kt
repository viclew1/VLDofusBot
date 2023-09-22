package fr.lewon.dofus.bot.gui.main.exploration.lastexploration

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.gui.main.exploration.lastexploration.impl.LastSubAreasExploration
import fr.lewon.dofus.bot.gui.main.exploration.lastexploration.impl.LastSubPathsExploration
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.paths.SubPath
import java.util.concurrent.locks.ReentrantLock

object LastExplorationUiUtil : ComposeUIUtil() {

    private val lock = ReentrantLock()
    private val uiState = mutableStateOf(LastExplorationUiState())

    fun getUiStateValue() = lock.executeSyncOperation {
        uiState.value
    }

    fun <T> updateExplorationProgress(
        character: DofusCharacter,
        item: T,
        current: Int,
        total: Int
    ) = lock.executeSyncOperation {
        val uiStateValue = uiState.value
        uiStateValue.lastExplorationByCharacter[character.name]?.let { lastExploration ->
            uiState.value = uiStateValue.copy(
                lastExplorationByCharacter = uiStateValue.lastExplorationByCharacter.plus(
                    character.name to getUpdatedLastExploration(
                        lastExploration as LastExploration<T>,
                        item,
                        current,
                        total
                    )
                )
            )
        }
    }

    @JvmName("onSubPathsExplorationStart")
    private fun <T> getUpdatedLastExploration(
        lastExploration: LastExploration<T>,
        item: T,
        current: Int,
        total: Int
    ): LastExploration<T> {
        return lastExploration.copy(
            progressByItem = lastExploration.progressByItem.plus(
                item to ExplorationProgress(current = current, total = total, started = true)
            )
        )
    }

    @JvmName("onSubAreasExplorationStart")
    fun onExplorationStart(
        character: DofusCharacter,
        subAreas: List<DofusSubArea>,
    ) = lock.executeSyncOperation {
        val uiStateValue = uiState.value
        uiState.value = uiStateValue.copy(
            lastExplorationByCharacter = uiStateValue.lastExplorationByCharacter.plus(
                character.name to LastSubAreasExploration(associateItemWithExplorationProgress(subAreas))
            )
        )
    }

    fun onExplorationStart(
        character: DofusCharacter,
        subPaths: List<SubPath>,
    ) = lock.executeSyncOperation {
        val uiStateValue = uiState.value
        uiState.value = uiStateValue.copy(
            lastExplorationByCharacter = uiStateValue.lastExplorationByCharacter.plus(
                character.name to LastSubPathsExploration(associateItemWithExplorationProgress(subPaths))
            )
        )
    }

    private fun <T> associateItemWithExplorationProgress(items: List<T>): Map<T, ExplorationProgress> {
        return items.associateWith {
            ExplorationProgress(0, 0, false)
        }
    }

    fun stopExploration(character: DofusCharacter) = lock.executeSyncOperation {
        val uiStateValue = uiState.value
        uiStateValue.lastExplorationByCharacter[character.name]?.let { lastExploration ->
            uiState.value = uiStateValue.copy(
                lastExplorationByCharacter = uiStateValue.lastExplorationByCharacter.plus(
                    character.name to lastExploration.copy(
                        explorationStopped = true
                    )
                )
            )
        }
    }

}