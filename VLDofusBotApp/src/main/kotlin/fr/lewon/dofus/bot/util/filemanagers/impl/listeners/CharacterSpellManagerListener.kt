package fr.lewon.dofus.bot.util.filemanagers.impl.listeners

import fr.lewon.dofus.bot.model.characters.spells.CharacterSpells

interface CharacterSpellManagerListener {

    fun onSpellsUpdate(characterName: String, spells: CharacterSpells)

}