package fr.lewon.dofus.bot.util

import fr.lewon.dofus.bot.model.characters.DofusCharacter

abstract class ListenableByCharacter<T> {

    private val listenersByCharacterName = HashMap<String, ArrayList<T>>()

    protected fun getListeners(character: DofusCharacter): List<T> {
        return getListeners(character.pseudo)
    }

    protected fun getListeners(characterName: String): List<T> {
        return listenersByCharacterName[characterName] ?: emptyList()
    }

    fun addListener(character: DofusCharacter, listener: T) {
        val characterListeners = listenersByCharacterName.computeIfAbsent(character.pseudo) { ArrayList() }
        characterListeners.add(listener)
    }

    fun removeListeners(character: DofusCharacter) {
        listenersByCharacterName[character.pseudo]?.clear()
    }

    fun removeListener(character: DofusCharacter, listener: T) {
        listenersByCharacterName[character.pseudo]?.remove(listener)
    }

    fun removeListener(listener: T) {
        listenersByCharacterName.forEach { it.value.remove(listener) }
    }

}