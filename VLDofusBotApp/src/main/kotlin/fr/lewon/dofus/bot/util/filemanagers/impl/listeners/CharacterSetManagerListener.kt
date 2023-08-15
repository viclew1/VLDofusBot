package fr.lewon.dofus.bot.util.filemanagers.impl.listeners

import fr.lewon.dofus.bot.model.characters.sets.CharacterSets

interface CharacterSetManagerListener {

    fun onSetsUpdate(characterName: String, sets: CharacterSets)

}