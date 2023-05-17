package fr.lewon.dofus.bot.game.fight.operations

class CooldownState(
    val globalTurnUseSpellStore: GlobalTurnUseSpellStore = GlobalTurnUseSpellStore(),
    val globalCooldownSpellStore: GlobalCooldownSpellStore = GlobalCooldownSpellStore()
) {

    fun deepCopy(): CooldownState {
        return CooldownState(
            globalTurnUseSpellStore.deepCopy(),
            globalCooldownSpellStore.deepCopy()
        )
    }
}