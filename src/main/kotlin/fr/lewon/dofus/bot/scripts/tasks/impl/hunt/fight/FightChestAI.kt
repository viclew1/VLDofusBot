package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.fight

import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffectGlobalType
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.ai.FightState
import fr.lewon.dofus.bot.game.fight.ai.complements.AIComplement
import fr.lewon.dofus.bot.game.fight.ai.impl.DefaultFightAI
import fr.lewon.dofus.bot.game.fight.operations.FightOperation
import fr.lewon.dofus.bot.game.fight.operations.FightOperationType

class FightChestAI(dofusBoard: DofusBoard, aiComplement: AIComplement) : DefaultFightAI(dofusBoard, aiComplement) {

    private companion object {
        private const val TO_HIT_CHEST_BONES_ID = 2672
    }

    override fun doGetNextOperation(fightBoard: FightBoard, initialState: FightState): FightOperation {
        val toHitMonster = fightBoard.getEnemyFighters().firstOrNull { it.bonesId == TO_HIT_CHEST_BONES_ID }
            ?: return FightOperation(FightOperationType.PASS_TURN)
        return initialState.getPossibleOperations().filter {
            it.targetCellId == toHitMonster.cell.cellId
                    && it.type == FightOperationType.SPELL
                    && it.spell != null
                    && isSingleAttackSpell(it.spell)
        }.minByOrNull { it.spell?.apCost ?: Int.MAX_VALUE } ?: FightOperation(FightOperationType.PASS_TURN)
    }

    private fun isSingleAttackSpell(spell: DofusSpellLevel): Boolean {
        return spell.effects.count { it.effectType.globalType == DofusSpellEffectGlobalType.ATTACK } == 1
    }

}