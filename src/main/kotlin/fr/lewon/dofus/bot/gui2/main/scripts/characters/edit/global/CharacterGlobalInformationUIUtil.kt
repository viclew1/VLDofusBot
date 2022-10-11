package fr.lewon.dofus.bot.gui2.main.scripts.characters.edit.global

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.utils.LockUtils
import java.util.concurrent.locks.ReentrantLock

object CharacterGlobalInformationUIUtil {

    private val lock = ReentrantLock()
    private val uiStateByCharacterName = HashMap<String, MutableState<CharacterGlobalInformationUIState>>()

    fun getCharacterGlobalInformationUIState(characterName: String): MutableState<CharacterGlobalInformationUIState> {
        return LockUtils.executeSyncOperation(lock) {
            uiStateByCharacterName.computeIfAbsent(characterName) {
                mutableStateOf(CharacterGlobalInformationUIState())
            }
        }
    }

    fun updateCharacterKamas(characterName: String, kamas: Long) {
        LockUtils.executeSyncOperation(lock) {
            val uiState = getCharacterGlobalInformationUIState(characterName)
            uiState.value = uiState.value.copy(kamasText = "${"%,d".format(kamas)} K")
        }
    }

    fun updateCharacterWeight(characterName: String, inventoryWeight: Int, weightMax: Int) {
        LockUtils.executeSyncOperation(lock) {
            val uiState = getCharacterGlobalInformationUIState(characterName)
            val percent = ((inventoryWeight.toFloat() / weightMax.toFloat()) * 100).toInt()
            uiState.value = uiState.value.copy(weightText = "$inventoryWeight / $weightMax ($percent%)")
        }
    }

    fun updateCharacterLevel(characterName: String, level: Int) {
        LockUtils.executeSyncOperation(lock) {
            val uiState = getCharacterGlobalInformationUIState(characterName)
            uiState.value = uiState.value.copy(levelText = "${minOf(200, level)}")
        }
    }

}