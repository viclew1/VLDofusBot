package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.model.characters.exploration.ExplorationRecord
import fr.lewon.dofus.bot.util.filemanagers.FileManager
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager

object ExplorationRecordManager : ToInitManager {

    private lateinit var fileManager: FileManager<ExplorationRecord>

    override fun initManager() {
        fileManager = FileManager("exploration_record", ExplorationRecord())
    }

    override fun getNeededManagers(): List<ToInitManager> = emptyList()

    fun getExploredTimeByMapId() = ExplorationRecord(fileManager.getStore())

    fun getLastExplorationTime(mapId: Double) = fileManager.getElement { it[mapId] }

    fun exploreMap(mapId: Double) = fileManager.updateStore { explorationRecord ->
        explorationRecord[mapId] = System.currentTimeMillis()
    }
}