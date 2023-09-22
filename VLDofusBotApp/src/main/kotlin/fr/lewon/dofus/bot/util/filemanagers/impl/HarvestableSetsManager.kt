package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.model.characters.jobs.HarvestableIdsBySetName
import fr.lewon.dofus.bot.model.jobs.HarvestJobs
import fr.lewon.dofus.bot.util.filemanagers.FileManager
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import java.util.concurrent.locks.ReentrantLock

object HarvestableSetsManager : ToInitManager {

    val defaultHarvestableIdsBySetName = mapOf(
        "Nothing" to emptySet(),
        "Everything" to HarvestJobs.entries.flatMap { it.items }.map { it.id }.toSet(),
    )

    private lateinit var fileManager: FileManager<HarvestableIdsBySetName>
    private val lock = ReentrantLock()

    override fun initManager() {
        fileManager = FileManager("harvestables", HarvestableIdsBySetName())
    }

    override fun getNeededManagers(): List<ToInitManager> = emptyList()

    fun addSet(setName: String) = lock.executeSyncOperation {
        fileManager.updateStore {
            if (!defaultHarvestableIdsBySetName.keys.contains(setName)) {
                it.computeIfAbsent(setName) { HashSet() }
            }
        }
    }

    fun addItemToHarvest(setName: String, itemId: Double) = lock.executeSyncOperation {
        fileManager.updateStore {
            val toHarvestItems = it[setName] ?: HashSet()
            it[setName] = toHarvestItems.plus(itemId)
        }
    }

    fun removeItemToHarvest(setName: String, itemId: Double) = lock.executeSyncOperation {
        fileManager.updateStore {
            val toHarvestItems = it[setName] ?: HashSet()
            it[setName] = toHarvestItems.minus(itemId)
        }
    }

    fun getItemsToHarvest(setName: String) = lock.executeSyncOperation {
        getHarvestableIdsBySetName()[setName]?.toList() ?: emptyList()
    }

    fun getHarvestableIdsBySetName() = lock.executeSyncOperation {
        defaultHarvestableIdsBySetName.plus(HarvestableIdsBySetName(fileManager.getStore()))
    }

    fun deleteSet(setName: String) = lock.executeSyncOperation {
        CharacterManager.getCharacters().forEach { character ->
            if (character.parameters.harvestableSet == setName) {
                CharacterManager.updateCharacter(
                    character.name, characterParameters = character.parameters.copy(
                        harvestableSet = defaultHarvestableIdsBySetName.keys.first()
                    )
                )
            }
        }
        fileManager.updateStore {
            it.remove(setName)
        }
    }

}