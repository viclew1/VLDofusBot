package fr.lewon.dofus.bot.util.listenable

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import java.util.concurrent.locks.ReentrantLock

abstract class ListenableByCharacter<T> {

    private val listenersByCharacterName = HashMap<String, HashSet<T>>()
    private val lock = ReentrantLock()

    protected fun getListeners(character: DofusCharacter): List<T> {
        return getListeners(character.name)
    }

    protected fun getListeners(characterName: String): List<T> {
        return lock.executeSyncOperation {
            listenersByCharacterName[characterName]?.toList() ?: emptyList()
        }
    }

    fun addListener(character: DofusCharacter, listener: T) {
        lock.executeSyncOperation {
            listenersByCharacterName.computeIfAbsent(character.name) { HashSet() }.add(listener)
        }
    }

    fun removeListeners(character: DofusCharacter) {
        lock.executeSyncOperation {
            listenersByCharacterName[character.name]?.clear()
        }
    }

    fun removeListener(character: DofusCharacter, listener: T) {
        lock.executeSyncOperation {
            removeListener(character.name, listener)
        }
    }

    fun removeListener(characterName: String, listener: T) {
        lock.executeSyncOperation {
            listenersByCharacterName[characterName]?.remove(listener)
        }
    }

    fun removeListener(listener: T) {
        lock.executeSyncOperation {
            listenersByCharacterName.forEach { it.value.remove(listener) }
        }
    }

}