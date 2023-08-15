package fr.lewon.dofus.bot.model.characters.sets

data class CharacterSetElement(
    var elementId: Int? = null,
    var key: Char = 'x',
    var ctrlModifier: Boolean = false,
)