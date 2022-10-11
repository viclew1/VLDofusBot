package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.core.utils.LockUtils
import fr.lewon.dofus.bot.model.characters.CharacterStore
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpells
import fr.lewon.dofus.bot.util.filemanagers.FileManager
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterManagerListener
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.script.ScriptRunner
import java.util.concurrent.locks.ReentrantLock

object CharacterManager : ToInitManager {

    private val listeners = ArrayList<CharacterManagerListener>()
    private val lock = ReentrantLock()
    private lateinit var fileManager: FileManager<CharacterStore>

    override fun initManager() {
        fileManager = FileManager("characters", CharacterStore())
    }

    override fun getNeededManagers(): List<ToInitManager> {
        return listOf(GlobalConfigManager)
    }

    fun addListener(listener: CharacterManagerListener) {
        LockUtils.executeSyncOperation(lock) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: CharacterManagerListener) {
        LockUtils.executeSyncOperation(lock) {
            listeners.remove(listener)
        }
    }

    fun getCharacter(name: String): DofusCharacter? {
        return LockUtils.executeSyncOperation(lock) {
            fileManager.getElement { store ->
                store.characters.firstOrNull {
                    it.name.lowercase() == name.lowercase()
                }
            }
        }
    }

    fun getCharacters(): List<DofusCharacter> {
        return LockUtils.executeSyncOperation(lock) {
            fileManager.getElement { store ->
                store.characters.toList()
            }
        }
    }

    fun getCharacters(selectedCharactersNames: List<String>): List<DofusCharacter> {
        return LockUtils.executeSyncOperation(lock) {
            fileManager.getElement { store ->
                store.characters.filter { it.name in selectedCharactersNames }
            }
        }
    }

    fun addCharacter(name: String, dofusClassId: Int, spells: List<CharacterSpell>): DofusCharacter {
        return LockUtils.executeSyncOperation(lock) {
            getCharacter(name)?.let {
                error("Character already registered : [$name]")
            }
            DofusCharacter(name, dofusClassId).also { character ->
                CharacterSpellManager.updateSpells(name, CharacterSpells(spells))
                fileManager.updateStore { store ->
                    store.characters.add(character)
                }
                listeners.toList().forEach { listener -> listener.onCharacterCreate(character) }
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
        ScriptValuesManager.removeScriptValues(character.name)
        CharacterSpellManager.removeSpells(character.name)
        ScriptRunner.removeListeners(character)
        GameSnifferUtil.removeListeners(character)
        listeners.toList().forEach { it.onCharacterDelete(character) }
    }

    fun updateCharacter(name: String, dofusClassId: Int? = null) {
        val storedCharacter = getCharacter(name)
            ?: error("Character not found in store : $name")
        storedCharacter.dofusClassId = dofusClassId ?: storedCharacter.dofusClassId
        fileManager.updateStore { }
        listeners.toList().forEach { it.onCharacterUpdate(storedCharacter) }
    }

}
