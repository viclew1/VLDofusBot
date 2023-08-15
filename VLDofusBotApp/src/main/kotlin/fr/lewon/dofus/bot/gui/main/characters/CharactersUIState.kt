package fr.lewon.dofus.bot.gui.main.characters

import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues

data class CharactersUIState(
    val characterNames: List<String>,
    val selectedCharacterNames: List<String> = emptyList(),
    val scriptParameterValues: ParameterValues = ParameterValues(),
)