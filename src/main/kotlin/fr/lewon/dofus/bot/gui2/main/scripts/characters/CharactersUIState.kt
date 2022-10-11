package fr.lewon.dofus.bot.gui2.main.scripts.characters

data class CharactersUIState(
    val characterNames: List<String>,
    val selectedCharacterName: String? = null,
)