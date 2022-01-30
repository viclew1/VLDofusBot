package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.operations.CooldownState
import fr.lewon.dofus.bot.game.fight.operations.FightOperation

class FightState(
    val fb: FightBoard,
    val cooldownState: CooldownState,
    val operations: ArrayList<FightOperation>,
    val playerAp: Int,
    val playerMp: Int
) {
    fun deepCopy(newAp: Int = playerAp, newMp: Int = playerMp): FightState {
        return FightState(fb.deepCopy(), cooldownState.deepCopy(), ArrayList(operations), newAp, newMp)
    }
}