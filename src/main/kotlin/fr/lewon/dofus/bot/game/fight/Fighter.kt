package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic
import kotlin.math.min

class Fighter(var cell: DofusCell, var id: Double, var isSummon: Boolean) {

    var maxHp = 0
    var hpLost = 0
    var hpHealed = 0
    var baseHp = 0
    val statsById: MutableMap<Int, CharacterCharacteristic> = HashMap()
    var teamId: Int = -1

    fun clone(): Fighter {
        return Fighter(cell, id, isSummon).also {
            it.maxHp = maxHp
            it.hpLost = hpLost
            it.hpHealed = hpHealed
            it.baseHp = baseHp
            it.teamId = teamId
            it.statsById.putAll(statsById)
        }
    }

    fun getCurrentHp(): Int {
        return min(maxHp, baseHp - hpLost + hpHealed)
    }
}