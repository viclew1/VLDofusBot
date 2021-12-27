package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic
import kotlin.math.min

class Fighter(var cell: DofusCell, var id: Double, var enemy: Boolean, var isSummon: Boolean) {

    var maxHp = 0
    var hpLost = 0
    var hpHealed = 0
    var baseHp = 0
    val statsById: MutableMap<Int, CharacterCharacteristic> = HashMap()

    fun clone(): Fighter {
        return Fighter(cell, id, enemy, isSummon).also { it.statsById.putAll(statsById) }
    }

    fun getCurrentHp(): Int {
        return min(maxHp, baseHp - hpLost + hpHealed)
    }
}