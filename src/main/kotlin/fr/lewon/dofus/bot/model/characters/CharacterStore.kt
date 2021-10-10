package fr.lewon.dofus.bot.model.characters

class CharacterStore(
    var characters: MutableList<DofusCharacter> = ArrayList(),
    var currentCharacterLogin: String? = null,
    var currentCharacterPseudo: String? = null
)