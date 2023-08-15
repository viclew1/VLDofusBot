package fr.lewon.dofus.bot.model.characters.sets

data class CharacterSet(
    var name: String = "",
    var spells: List<CharacterSetElement> = emptyList(),
    var items: List<CharacterSetElement> = emptyList(),
)