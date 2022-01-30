package fr.lewon.dofus.bot.game.fight.operations

import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel

class CooldownState(
    val turnSpellUseStore: TurnSpellUseStore = TurnSpellUseStore(),
    val cdBySpell: HashMap<DofusSpellLevel, Int> = HashMap()
) {
    fun deepCopy(): CooldownState {
        return CooldownState(
            turnSpellUseStore.deepCopy(),
            HashMap(cdBySpell)
        )
    }
}