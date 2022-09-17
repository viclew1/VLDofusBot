package fr.lewon.dofus.bot.gui2.main.scripts.characters

import fr.lewon.dofus.bot.model.characters.DofusCharacter

data class CharactersUIState(
    val characters: List<DofusCharacter>,
    val selectedCharacter: DofusCharacter? = null,
)