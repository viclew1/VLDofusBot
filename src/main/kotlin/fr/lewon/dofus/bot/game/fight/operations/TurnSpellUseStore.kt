package fr.lewon.dofus.bot.game.fight.operations

import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel

class TurnSpellUseStore : HashMap<DofusSpellLevel, HashMap<Double, Int>>() {

    fun getTotalUses(spell: DofusSpellLevel): Int {
        return this[spell]?.values?.sum() ?: 0
    }

    fun getUsesOnTarget(spell: DofusSpellLevel, targetId: Double): Int {
        return this[spell]?.get(targetId) ?: 0
    }

    fun deepCopy(): TurnSpellUseStore {
        val copy = TurnSpellUseStore()
        for (e in entries) {
            copy[e.key] = HashMap(e.value)
        }
        return copy
    }

}