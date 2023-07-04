package fr.lewon.dofus.bot.gui.main.exploration.lastexploration

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import java.util.concurrent.locks.ReentrantLock

object LastExplorationUiUtil : ComposeUIUtil() {

    private val lock = ReentrantLock()
    private val uiState = mutableStateOf(LastExplorationUiState())

    fun getUiStateValue() = lock.executeSyncOperation {
        uiState.value
    }

    fun updateExplorationProgress(
        character: DofusCharacter,
        currentSubArea: DofusSubArea? = null,
        toExploreCount: Int? = null,
        progress: Int
    ) = lock.executeSyncOperation {
        val uiStateValue = uiState.value
        uiStateValue.lastExplorationByCharacter[character.name]?.let { lastExploration ->
            uiState.value = uiStateValue.copy(
                lastExplorationByCharacter = uiStateValue.lastExplorationByCharacter.plus(
                    character.name to lastExploration.copy(
                        currentSubAreaId = currentSubArea?.id ?: lastExploration.currentSubAreaId,
                        currentAreaTotalCount = toExploreCount ?: lastExploration.currentAreaTotalCount,
                        currentAreaProgress = progress
                    )
                )
            )
        }
    }

    fun onExplorationStart(
        character: DofusCharacter,
        subAreas: List<DofusSubArea>,
    ) = lock.executeSyncOperation {
        val uiStateValue = uiState.value
        uiState.value = uiStateValue.copy(
            lastExplorationByCharacter = uiStateValue.lastExplorationByCharacter.plus(
                character.name to LastExploration(subAreas)
            )
        )
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

    fun finishExploration(character: DofusCharacter) = lock.executeSyncOperation {
        val uiStateValue = uiState.value
        uiStateValue.lastExplorationByCharacter[character.name]?.let { lastExploration ->
            uiState.value = uiStateValue.copy(
                lastExplorationByCharacter = uiStateValue.lastExplorationByCharacter.plus(
                    character.name to lastExploration.copy(
                        explorationFinished = true
                    )
                )
            )
        }
    }

}