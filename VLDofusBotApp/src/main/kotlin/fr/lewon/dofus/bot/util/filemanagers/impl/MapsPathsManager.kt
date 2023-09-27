package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.model.characters.paths.MapsPath
import fr.lewon.dofus.bot.model.characters.paths.MapsPathByName
import fr.lewon.dofus.bot.model.characters.paths.SubPath
import fr.lewon.dofus.bot.util.filemanagers.FileManager
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.MapsPathsManagerListener
import fr.lewon.dofus.bot.util.listenable.Listenable
import java.util.concurrent.locks.ReentrantLock

object MapsPathsManager : Listenable<MapsPathsManagerListener>(), ToInitManager {

    private lateinit var fileManager: FileManager<MapsPathByName>
    private val lock = ReentrantLock()

    override fun initManager() {
        fileManager = FileManager("paths", MapsPathByName())
    }

    override fun getNeededManagers(): List<ToInitManager> = emptyList()

    fun addPath(pathName: String) = lock.executeSyncOperation {
        fileManager.updateStore {
            it.computeIfAbsent(pathName) { MapsPath(pathName) }
        }
        onPathsUpdate()
    }

    fun updatePath(pathName: String, mapsPath: MapsPath) = lock.executeSyncOperation {
        fileManager.updateStore {
            it[pathName] = mapsPath
        }
        onPathsUpdate()
    }

    fun updateSubPath(pathName: String, subPathId: String, updatedSubPath: SubPath) = lock.executeSyncOperation {
        val path = getPathByName()[pathName]
        if (path != null) {
            val subPathIndex = path.subPaths.indexOfFirst { subPath -> subPath.id == subPathId }
            if (subPathIndex >= 0) {
                val newSubPaths = path.subPaths.toMutableList()
                newSubPaths[subPathIndex] = updatedSubPath
                updatePath(pathName, path.copy(subPaths = newSubPaths))
            }
        }
    }

    fun getPathByName() = lock.executeSyncOperation {
        MapsPathByName(fileManager.getStore()).toSortedMap()
    }

    fun deletePath(pathName: String) = lock.executeSyncOperation {
        fileManager.updateStore {
            it.remove(pathName)
        }
        onPathsUpdate()
    }

    private fun onPathsUpdate() {
        val pathsByName = getPathByName()
        getListeners().forEach { it.onPathsUpdate(pathsByName) }
    }

}