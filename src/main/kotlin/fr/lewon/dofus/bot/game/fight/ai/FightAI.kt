package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.ai.complements.AIComplement
import fr.lewon.dofus.bot.game.fight.operations.CooldownState
import fr.lewon.dofus.bot.game.fight.operations.FightOperation

abstract class FightAI(protected val dofusBoard: DofusBoard, protected val aiComplement: AIComplement) {

    protected val cooldownState = CooldownState()
    protected var currentTurn = 0

    fun onFightStart(fightBoard: FightBoard) {
        currentTurn = 0
        val playerFighter = fightBoard.getPlayerFighter() ?: error("Couldn't find player fighter")
        val cooldownSpellStore = cooldownState.globalCooldownSpellStore.getCooldownSpellStore(playerFighter.id)
        playerFighter.spells.filter { it.initialCooldown > 0 }.forEach {
            cooldownSpellStore[it] = it.initialCooldown + 1
        }
    }

    abstract fun selectStartCell(fightBoard: FightBoard): DofusCell?

    fun onNewTurn(fightBoard: FightBoard) {
        currentTurn++
        cooldownState.globalTurnUseSpellStore.clear()
        val toRemove = ArrayList<DofusSpellLevel>()
        for (cooldownSpellStore in cooldownState.globalCooldownSpellStore.values) {
            cooldownSpellStore.entries.forEach {
                val newValue = it.value - 1
                if (newValue > 0) {
                    cooldownSpellStore[it.key] = newValue
                } else {
                    toRemove.add(it.key)
                }
            }
            toRemove.forEach { cooldownSpellStore.remove(it) }
        }
        fightBoard.getPlayerFighter()?.let {
            it.totalMp = DofusCharacteristics.MOVEMENT_POINTS.getValue(it)
        }
    }

    fun getNextOperation(fightBoard: FightBoard, lastOperation: FightOperation?): FightOperation {
        fightBoard.getPlayerFighter() ?: error("No player fighter found")
        val dangerMap = aiComplement.buildDangerMap(dofusBoard, fightBoard.deepCopy())
        val initialState = FightState(
            fightBoard.deepCopy(), cooldownState, aiComplement, dofusBoard, dangerMap, lastOperation = lastOperation
        )
        val operation = doGetNextOperation(fightBoard, initialState)
        initialState.makeMove(operation)
        return operation
    }

    protected abstract fun doGetNextOperation(fightBoard: FightBoard, initialState: FightState): FightOperation

}