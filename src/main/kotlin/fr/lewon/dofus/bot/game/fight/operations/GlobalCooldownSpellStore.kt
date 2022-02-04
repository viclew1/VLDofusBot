package fr.lewon.dofus.bot.game.fight.operations

class GlobalCooldownSpellStore : HashMap<Double, CooldownSpellStore>() {

    fun getCooldownSpellStore(fighterId: Double): CooldownSpellStore {
        return computeIfAbsent(fighterId) { CooldownSpellStore() }
    }

    fun deepCopy(): GlobalCooldownSpellStore {
        val copy = GlobalCooldownSpellStore()
        entries.forEach {
            copy[it.key] = it.value.deepCopy()
        }
        return copy
    }
}