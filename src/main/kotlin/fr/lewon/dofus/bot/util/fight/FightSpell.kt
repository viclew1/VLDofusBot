package fr.lewon.dofus.bot.util.fight

class FightSpell(
    var minRange: Int = 0,
    var maxRange: Int = 10,
    var initialCooldown: Int = 0,
    var cooldown: Int = 0,
    var hotkey: Char = '&',
    var usesPerTurn: Int = 2,
    var cost: Int = 3,
    var needsLos: Boolean = true
)