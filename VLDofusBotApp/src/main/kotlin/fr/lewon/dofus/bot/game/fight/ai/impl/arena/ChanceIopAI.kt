package fr.lewon.dofus.bot.game.fight.ai.impl.arena

import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.ai.FightState
import fr.lewon.dofus.bot.game.fight.ai.complements.DefaultAIComplement
import fr.lewon.dofus.bot.game.fight.ai.impl.ArenaAI
import fr.lewon.dofus.bot.game.fight.operations.FightOperation
import fr.lewon.dofus.bot.game.fight.operations.FightOperationType

class ChanceIopAI(dofusBoard: DofusBoard) : ArenaAI(dofusBoard, DefaultAIComplement()) {

    companion object {
        private val VERTU_SPELL_NAME = "vertu"
        private val WEAPON_MASTERY_SPELL_NAME = "maîtrise d'arme"
        private val DIVINE_SWORD_SPELL_NAME = "Épée divine"
        private val POWER_SPELL_NAME = "puissance"
    }

    override fun doGetNextOperation(fightBoard: FightBoard, initialState: FightState): FightOperation {
        return when (currentTurn) {
            1 -> getNextFirstTurnBuff(fightBoard)
            else -> TODO()
        }
    }

    private fun getNextFirstTurnBuff(fightBoard: FightBoard): FightOperation {
        val playerFighter = fightBoard.getPlayerFighter()
            ?: error("Missing player fighter")
        val turnUseSpellStore = cooldownState.globalTurnUseSpellStore.getTurnUseSpellStore(playerFighter.id)
        val vertuSpell = getSpellByName(playerFighter, VERTU_SPELL_NAME)
        if (!turnUseSpellStore.containsKey(vertuSpell)) {
            return FightOperation(FightOperationType.SPELL, playerFighter.cell.cellId, vertuSpell)
        }
        val weaponMasterySpell = getSpellByName(playerFighter, WEAPON_MASTERY_SPELL_NAME)
        if (!turnUseSpellStore.containsKey(weaponMasterySpell)) {
            return FightOperation(FightOperationType.SPELL, playerFighter.cell.cellId, weaponMasterySpell)
        }
        val divineSwordSpell = getSpellByName(playerFighter, DIVINE_SWORD_SPELL_NAME)
        if (!turnUseSpellStore.containsKey(divineSwordSpell)) {
            return FightOperation(FightOperationType.SPELL, playerFighter.cell.cellId, divineSwordSpell)
        }
        val powerSpell = getSpellByName(playerFighter, POWER_SPELL_NAME)
        if (!turnUseSpellStore.containsKey(powerSpell)) {
            return FightOperation(FightOperationType.SPELL, playerFighter.cell.cellId, powerSpell)
        }
        return FightOperation(FightOperationType.PASS_TURN)
    }

}