package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.model.characters.jobs.HarvestableIdsBySetName
import fr.lewon.dofus.bot.model.jobs.HarvestJobs
import fr.lewon.dofus.bot.scripts.impl.ExploreAreaScriptBuilder
import fr.lewon.dofus.bot.util.filemanagers.FileManager
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterSpellManagerListener
import fr.lewon.dofus.bot.util.listenable.ListenableByCharacter
import java.util.concurrent.locks.ReentrantLock

object HarvestableSetsManager : ListenableByCharacter<CharacterSpellManagerListener>(), ToInitManager {

    val defaultHarvestableIdsBySetName = mapOf(
        "Nothing" to emptySet(),
        "Everything" to HarvestJobs.values().flatMap { it.items }.map { it.id }.toSet(),
    )

    private lateinit var fileManager: FileManager<HarvestableIdsBySetName>
    private val lock = ReentrantLock()

    override fun initManager() {
        fileManager = FileManager("harvestables", HarvestableIdsBySetName())
    }

    override fun getNeededManagers(): List<ToInitManager> {
        return listOf(CharacterManager)
    }

    fun addSet(setName: String) = lock.executeSyncOperation {
        fileManager.updateStore {
            if (!defaultHarvestableIdsBySetName.keys.contains(setName)) {
                it.computeIfAbsent(setName) { HashSet() }
            }
        }
        ExploreAreaScriptBuilder.harvestParameter.possibleValues = getHarvestableIdsBySetName().keys.toList()
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
        fileManager.updateStore {
            it.remove(setName)
        }
        ExploreAreaScriptBuilder.harvestParameter.possibleValues = getHarvestableIdsBySetName().keys.toList()
    }

}