package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpells
import fr.lewon.dofus.bot.model.characters.spells.SpellStore
import fr.lewon.dofus.bot.util.filemanagers.FileManager
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterSpellManagerListener
import fr.lewon.dofus.bot.util.listenable.ListenableByCharacter
import java.util.concurrent.locks.ReentrantLock

object CharacterSpellManager : ListenableByCharacter<CharacterSpellManagerListener>(), ToInitManager {

    private lateinit var fileManager: FileManager<SpellStore>
    private val lock = ReentrantLock()

    override fun initManager() {
        fileManager = FileManager("spells", SpellStore())
    }

    override fun getNeededManagers(): List<ToInitManager> {
        return listOf(CharacterManager)
    }

    fun getSpells(character: DofusCharacter): CharacterSpells {
        return getSpells(character.name)
    }

    fun getSpells(characterName: String): CharacterSpells {
        return lock.executeSyncOperation {
            fileManager.getElement { store ->
                store.getCharacterSpells(characterName).deepCopy()
            }
        }
    }

    fun updateSpells(characterName: String, spells: CharacterSpells) {
        lock.executeSyncOperation {
            fileManager.updateStore { store ->
                store.setCharacterSpells(characterName, spells.deepCopy())
            }
            getListeners(characterName).forEach { it.onSpellsUpdate(characterName, spells) }
        }
    }

    fun updateSpell(characterName: String, key: Char, ctrlModifier: Boolean, spellId: Int?) {
        lock.executeSyncOperation {
            val spells = getSpells(characterName)
            spells.removeIf { it.key == key && it.ctrlModifier == ctrlModifier }
            spells.add(CharacterSpell(spellId, key, ctrlModifier))
            fileManager.updateStore { store ->
                store.setCharacterSpells(characterName, spells)
            }
            getListeners(characterName).forEach { it.onSpellsUpdate(characterName, spells) }
        }
    }

    fun removeSpells(characterName: String) {
        lock.executeSyncOperation {
            fileManager.updateStore { store ->
                store.remove(characterName)
            }
        }
    }

}