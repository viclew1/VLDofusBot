package fr.lewon.dofus.bot.game.fight.operations

import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel

class TurnUseSpellStore : HashMap<DofusSpellLevel, HashMap<Double, Int>>() {

    fun getTotalUses(spell: DofusSpellLevel): Int {
        return this[spell]?.values?.sum() ?: 0
    }

    fun getUsesOnTarget(spell: DofusSpellLevel, targetId: Double): Int {
        return this[spell]?.get(targetId) ?: 0
    }

    fun deepCopy(): TurnUseSpellStore {
        val copy = TurnUseSpellStore()
        for (e in entries) {
            copy[e.key] = HashMap(e.value)
        }
        return copy
    }

}