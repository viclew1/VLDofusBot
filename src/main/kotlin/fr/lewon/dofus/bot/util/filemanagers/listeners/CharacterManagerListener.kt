package fr.lewon.dofus.bot.util.filemanagers.listeners

import fr.lewon.dofus.bot.model.characters.DofusCharacter

interface CharacterManagerListener {

    fun onCharacterCreate(character: DofusCharacter)

    fun onCharacterMove(character: DofusCharacter, toIndex: Int)

    fun onCharacterDelete(character: DofusCharacter)

}