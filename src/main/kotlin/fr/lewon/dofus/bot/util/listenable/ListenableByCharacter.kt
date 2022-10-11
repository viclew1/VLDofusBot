package fr.lewon.dofus.bot.util.listenable

import fr.lewon.dofus.bot.model.characters.DofusCharacter

abstract class ListenableByCharacter<T> {

    private val listenersByCharacterName = HashMap<String, HashSet<T>>()

    protected fun getListeners(character: DofusCharacter): List<T> {
        return getListeners(character.name)
    }

    protected fun getListeners(characterName: String): List<T> {
        return listenersByCharacterName[characterName]?.toList() ?: emptyList()
    }

    fun addListener(character: DofusCharacter, listener: T) {
        listenersByCharacterName.computeIfAbsent(character.name) { HashSet() }.add(listener)
    }

    fun removeListeners(character: DofusCharacter) {
        listenersByCharacterName[character.name]?.clear()
    }

    fun removeListener(character: DofusCharacter, listener: T) {
        removeListener(character.name, listener)
    }

    fun removeListener(characterName: String, listener: T) {
        listenersByCharacterName[characterName]?.remove(listener)
    }

    fun removeListener(listener: T) {
        listenersByCharacterName.forEach { it.value.remove(listener) }
    }

}