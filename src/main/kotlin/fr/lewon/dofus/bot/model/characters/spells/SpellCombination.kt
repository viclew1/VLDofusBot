package fr.lewon.dofus.bot.model.characters.spells

data class SpellCombination(
    var type: SpellType = SpellType.ATTACK,
    var keys: String = "",
    var minRange: Int = 0,
    var maxRange: Int = 0,
    var needsLos: Boolean = true,
    var castInDiagonal: Boolean = false,
    var castInLine: Boolean = false,
    var modifiableRange: Boolean = false,
    var cooldown: Int = 0,
    var apCost: Int = 0,
    var usesPerTurn: Int = 0,
    var usesPerTurnPerTarget: Int = 0,
    var amount: Int = 0,
    var aiWeight: Int = 0,
    var areaType: AreaType = AreaType.CIRCLE,
    var areaSize: Int = 0,
    var needsHit: Boolean = true,
    var canHit: Boolean = true,
    var dashToward: Boolean = false,
    var dashLength: Int = 0,
    var canReachCell: Boolean = true,
    var canTargetEmpty: Boolean = true
)