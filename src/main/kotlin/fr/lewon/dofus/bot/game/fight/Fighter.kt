package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic
import kotlin.math.min

class Fighter(
    var cell: DofusCell,
    var id: Double,
    var isSummon: Boolean,
    var spells: List<DofusSpellLevel> = ArrayList(),
    val baseStatsById: MutableMap<Int, CharacterCharacteristic> = HashMap(),
    val statsById: MutableMap<Int, CharacterCharacteristic> = HashMap()
) {

    var maxHp = 0
    var hpLost = 0
    var hpHealed = 0
    var baseHp = 0
    var teamId: Int = -1

    fun deepCopy(): Fighter {
        return Fighter(cell, id, isSummon, spells, HashMap(baseStatsById), HashMap(statsById)).also {
            it.maxHp = maxHp
            it.hpLost = hpLost
            it.hpHealed = hpHealed
            it.baseHp = baseHp
            it.teamId = teamId
        }
    }

    fun getCurrentHp(): Int {
        return min(maxHp, baseHp - hpLost + hpHealed)
    }
}