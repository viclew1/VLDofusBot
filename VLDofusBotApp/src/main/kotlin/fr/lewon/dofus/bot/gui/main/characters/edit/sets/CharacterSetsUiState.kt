package fr.lewon.dofus.bot.gui.main.characters.edit.sets

import fr.lewon.dofus.bot.model.characters.sets.CharacterSets

data class CharacterSetsUiState(
    val setsByCharacterName: Map<String, CharacterSets> = emptyMap(),
)