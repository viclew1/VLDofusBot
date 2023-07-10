package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.model.characters.exploration.ExplorationRecord
import fr.lewon.dofus.bot.util.filemanagers.FileManager
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import java.util.concurrent.locks.ReentrantLock

object ExplorationRecordManager : ToInitManager {

    private lateinit var fileManager: FileManager<ExplorationRecord>
    private val lock = ReentrantLock()

    override fun initManager() {
        fileManager = FileManager("exploration_record", ExplorationRecord())
    }

    override fun getNeededManagers(): List<ToInitManager> = emptyList()

    fun getExploredTimeByMapId() = lock.executeSyncOperation {
        ExplorationRecord(fileManager.getStore())
    }

    fun getLastExplorationTime(mapId: Double) = lock.executeSyncOperation {
        fileManager.getElement { it[mapId] }
    }

    fun exploreMap(mapId: Double) = lock.executeSyncOperation {
        fileManager.updateStore { explorationRecord ->
            explorationRecord[mapId] = System.currentTimeMillis()
        }
    }
}