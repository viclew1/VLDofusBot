package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.model.characters.sets.CharacterSet
import fr.lewon.dofus.bot.model.characters.sets.CharacterSetStore
import fr.lewon.dofus.bot.model.characters.sets.CharacterSets
import fr.lewon.dofus.bot.util.filemanagers.FileManager
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterSetManagerListener
import fr.lewon.dofus.bot.util.listenable.ListenableByCharacter
import java.util.concurrent.locks.ReentrantLock

object CharacterSetsManager : ListenableByCharacter<CharacterSetManagerListener>(), ToInitManager {

    const val DefaultSetName = "Default Set"

    private lateinit var fileManager: FileManager<CharacterSetStore>
    private val lock = ReentrantLock()

    override fun initManager() {
        fileManager = FileManager("character_sets", CharacterSetStore())
    }

    override fun getNeededManagers(): List<ToInitManager> = emptyList()

    fun setSelectedSet(characterName: String, setName: String) = lock.executeSyncOperation {
        fileManager.updateStore { store ->
            store.selectSet(characterName, setName)
        }
        getListeners(characterName).forEach {
            it.onSetsUpdate(characterName, getSets(characterName))
        }
    }

    fun updateSet(characterName: String, set: CharacterSet) = lock.executeSyncOperation {
        fileManager.updateStore { store ->
            store.updateCharacterSet(characterName, set)
        }
        getListeners(characterName).forEach {
            it.onSetsUpdate(characterName, getSets(characterName))
        }
    }

    fun getSelectedSet(characterName: String) = lock.executeSyncOperation {
        fileManager.getElement { store ->
            val characterSets = store.getCharacterSets(characterName)
            characterSets.sets.firstOrNull { it.name == characterSets.selectedSetName }
                ?: CharacterSet("INVALID_SET")
        }
    }

    fun getSets(characterName: String): CharacterSets = lock.executeSyncOperation {
        val sets = fileManager.getElement {
            it.getCharacterSets(characterName)
        }
        if (sets.sets.isEmpty()) {
            updateSet(characterName, CharacterSet(DefaultSetName))
            getSets(characterName)
        } else sets
    }

    fun deleteSet(characterName: String, setName: String) = lock.executeSyncOperation {
        if (setName != DefaultSetName) {
            fileManager.updateStore { store ->
                store.removeSet(characterName, setName)
            }
            getListeners(characterName).forEach {
                it.onSetsUpdate(characterName, getSets(characterName))
            }
        }
    }

    fun deleteSets(characterName: String) = lock.executeSyncOperation {
        fileManager.updateStore { store ->
            store.remove(characterName)
        }
    }

}