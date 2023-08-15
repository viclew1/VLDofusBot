package fr.lewon.dofus.bot.gui.main.characters.edit.global

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import java.util.concurrent.locks.ReentrantLock

object CharacterGlobalInformationUIUtil : ComposeUIUtil() {

    private val lock = ReentrantLock()
    private val uiStateByCharacterName = HashMap<String, MutableState<CharacterGlobalInformationUIState>>()

    fun getCharacterGlobalInformationUIState(characterName: String): MutableState<CharacterGlobalInformationUIState> {
        return lock.executeSyncOperation {
            uiStateByCharacterName.computeIfAbsent(characterName) {
                mutableStateOf(CharacterGlobalInformationUIState())
            }
        }
    }

    fun updateCharacterKamas(characterName: String, kamas: Long) {
        lock.executeSyncOperation {
            val uiState = getCharacterGlobalInformationUIState(characterName)
            uiState.value = uiState.value.copy(kamasText = "${"%,d".format(kamas)} K")
        }
    }

    fun updateCharacterWeight(characterName: String, inventoryWeight: Int, weightMax: Int) {
        lock.executeSyncOperation {
            val uiState = getCharacterGlobalInformationUIState(characterName)
            val percent = ((inventoryWeight.toFloat() / weightMax.toFloat()) * 100).toInt()
            uiState.value = uiState.value.copy(weightText = "$inventoryWeight / $weightMax ($percent%)")
        }
    }

    fun updateCharacterLevel(characterName: String, level: Int) {
        lock.executeSyncOperation {
            val uiState = getCharacterGlobalInformationUIState(characterName)
            uiState.value = uiState.value.copy(levelText = "${minOf(200, level)}")
        }
    }

}