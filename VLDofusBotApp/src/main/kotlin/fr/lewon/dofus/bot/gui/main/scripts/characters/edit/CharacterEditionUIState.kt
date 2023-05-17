package fr.lewon.dofus.bot.gui.main.scripts.characters.edit

data class CharacterEditionUIState(
    val characterName: String? = null,
    val editionTab: EditionTab = EditionTab.SPELLS,
)