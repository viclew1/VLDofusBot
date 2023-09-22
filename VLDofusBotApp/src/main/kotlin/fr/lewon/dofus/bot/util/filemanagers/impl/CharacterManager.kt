package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.model.characters.CharacterStore
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.DofusCharacterParameters
import fr.lewon.dofus.bot.model.characters.sets.CharacterSet
import fr.lewon.dofus.bot.util.filemanagers.FileManager
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterManagerListener
import fr.lewon.dofus.bot.util.listenable.Listenable
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.script.ScriptRunner
import java.util.concurrent.locks.ReentrantLock

object CharacterManager : Listenable<CharacterManagerListener>(), ToInitManager {

    private val lock = ReentrantLock()
    private lateinit var fileManager: FileManager<CharacterStore>

    override fun initManager() {
        fileManager = FileManager("characters", CharacterStore())
    }

    override fun getNeededManagers(): List<ToInitManager> {
        return listOf(GlobalConfigManager)
    }

    fun getCharacter(name: String): DofusCharacter? {
        return lock.executeSyncOperation {
            fileManager.getElement { store ->
                store.characters.firstOrNull {
                    it.name.lowercase() == name.lowercase()
                }
            }
        }
    }

    fun getCharacters(): List<DofusCharacter> {
        return lock.executeSyncOperation {
            fileManager.getElement { store ->
                store.characters.toList()
            }
        }
    }

    fun getCharacters(selectedCharactersNames: List<String>): List<DofusCharacter> {
        return lock.executeSyncOperation {
            fileManager.getElement { store ->
                store.characters.filter { it.name in selectedCharactersNames }
            }
        }
    }

    fun addCharacter(name: String, dofusClassId: Int): DofusCharacter {
        return lock.executeSyncOperation {
            getCharacter(name)?.let {
                error("Character already registered : [$name]")
            }
            DofusCharacter(name, dofusClassId).also { character ->
                CharacterSetsManager.updateSet(name, CharacterSet(CharacterSetsManager.DefaultSetName))
                fileManager.updateStore { store ->
                    store.characters.add(character)
                }
                getListeners().forEach { listener -> listener.onCharacterCreate(character) }
            }
        }
    }

    fun removeCharacter(name: String) {
        removeCharacter(getCharacter(name) ?: error("Character not found : $name"))
    }

    fun removeCharacter(character: DofusCharacter) {
        fileManager.updateStore { store ->
            store.characters.remove(character)
        }
        CharacterSetsManager.deleteSets(character.name)
        ScriptRunner.removeListeners(character)
        GameSnifferUtil.removeListeners(character)
        getListeners().forEach { it.onCharacterDelete(character) }
    }

    fun updateCharacter(
        name: String,
        dofusClassId: Int? = null,
        characterParameters: DofusCharacterParameters? = null,
    ) {
        val storedCharacter = getCharacter(name)
            ?: error("Character not found in store : $name")
        storedCharacter.dofusClassId = dofusClassId
            ?: storedCharacter.dofusClassId
        storedCharacter.parameters = characterParameters
            ?: storedCharacter.parameters
        fileManager.updateStore { }
        getListeners().forEach { it.onCharacterUpdate(storedCharacter) }
    }

}
