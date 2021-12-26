package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic

class Fighter(var cell: DofusCell, var id: Double, var enemy: Boolean, var isSummon: Boolean) {

    var maxHp = 0
    var hpLost = 0
    var hpHealed = 0
    var hp = 0
    val statsById: MutableMap<Int, CharacterCharacteristic> = HashMap()

    fun clone(): Fighter {
        return Fighter(cell, id, enemy, isSummon).also { it.statsById.putAll(statsById) }
    }
}