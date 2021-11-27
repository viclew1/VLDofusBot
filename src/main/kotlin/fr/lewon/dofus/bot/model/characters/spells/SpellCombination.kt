package fr.lewon.dofus.bot.model.characters.spells

data class SpellCombination(
    var type: SpellType = SpellType.ATTACK,
    var keys: String = "",
    var minRange: Int = -1,
    var maxRange: Int = -1,
    var needsLos: Boolean = true,
    var castInLine: Boolean = false,
    var modifiableRange: Boolean = false,
    var cooldown: Int = 0,
    var apCost: Int = 0,
    var usesPerTurn: Int = 0,
    var amount: Int = 0,
    var aiWeight: Int = 0
)