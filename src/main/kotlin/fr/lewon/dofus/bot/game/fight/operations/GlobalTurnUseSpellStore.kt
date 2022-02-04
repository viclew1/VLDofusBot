package fr.lewon.dofus.bot.game.fight.operations

class GlobalTurnUseSpellStore : HashMap<Double, TurnSpellUseStore>() {

    fun getTurnSpellUseStore(fighterId: Double): TurnSpellUseStore {
        return computeIfAbsent(fighterId) { TurnSpellUseStore() }
    }

    fun deepCopy(): GlobalTurnUseSpellStore {
        val copy = GlobalTurnUseSpellStore()
        entries.forEach {
            copy[it.key] = it.value.deepCopy()
        }
        return copy
    }

}