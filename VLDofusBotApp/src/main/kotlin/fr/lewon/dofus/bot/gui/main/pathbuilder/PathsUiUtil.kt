package fr.lewon.dofus.bot.gui.main.pathbuilder

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.paths.MapsPath
import fr.lewon.dofus.bot.model.characters.paths.MapsPathByName
import fr.lewon.dofus.bot.model.characters.paths.SubPath
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.MapsPathsManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.MapsPathsManagerListener
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import java.util.concurrent.locks.ReentrantLock

object PathsUiUtil : ComposeUIUtil(), MapsPathsManagerListener {

    private val uiState = mutableStateOf(PathsUiState())
    private val lock = ReentrantLock()

    override fun init() {
        MapsPathsManager.addListener(this)
    }

    fun getUiState() = lock.executeSyncOperation { uiState.value }

    override fun onPathsUpdate(mapPathsByName: MapsPathByName) {
        uiState.value = uiState.value.copy(mapsPaths = mapPathsByName.values.toList())
    }

    fun setSelectedPath(selectedPathName: String?) = lock.executeSyncOperation {
        val uiStateValue = getUiState()
        val currentPath = uiStateValue.selectedPathName
        uiState.value = uiState.value.copy(selectedPathName = selectedPathName)
        if (currentPath != selectedPathName) {
            registerSubPath(null)
        }
    }

    fun setSelectedCharacterName(selectedCharacterName: String?) = lock.executeSyncOperation {
        uiState.value = uiState.value.copy(selectedCharacterName = selectedCharacterName)
    }

    fun registerSubPath(subPath: SubPath?) = lock.executeSyncOperation {
        uiState.value = uiState.value.copy(registeredSubPathId = subPath?.id)
    }

    fun startRegisteringSubPath(path: MapsPath) = lock.executeSyncOperation {
        val uiStateValue = getUiState()
        val selectedCharacter = uiStateValue.selectedCharacterName?.let { CharacterManager.getCharacter(it) }
        if (selectedCharacter != null) {
            val connection = GameSnifferUtil.getFirstConnection(selectedCharacter)
            val gameInfo = connection?.let { GameSnifferUtil.getGameInfoByConnection(it) }
            if (gameInfo != null && !gameInfo.isInHavenBag) {
                val newSubPath = SubPath(pathName = path.name, mapIds = listOf(gameInfo.currentMap.id))
                MapsPathsManager.updatePath(path.name, path.copy(subPaths = path.subPaths.plus(newSubPath)))
                registerSubPath(newSubPath)
            }
        }
    }

    fun addMapToRegisteredSubPath(character: DofusCharacter, mapId: Double) = lock.executeSyncOperation {
        val uiStateValue = getUiState()
        if (uiStateValue.registeredSubPathId != null && character.name == uiStateValue.selectedCharacterName) {
            val selectedPath = uiStateValue.mapsPaths.firstOrNull { it.name == uiStateValue.selectedPathName }
            if (selectedPath != null) {
                val registeredSubPath = selectedPath.subPaths.firstOrNull { it.id == uiStateValue.registeredSubPathId }
                if (registeredSubPath != null) {
                    MapsPathsManager.updateSubPath(
                        selectedPath.name,
                        uiStateValue.registeredSubPathId,
                        registeredSubPath.copy(mapIds = registeredSubPath.mapIds.plus(mapId))
                    )
                }
            }
        }
    }
}