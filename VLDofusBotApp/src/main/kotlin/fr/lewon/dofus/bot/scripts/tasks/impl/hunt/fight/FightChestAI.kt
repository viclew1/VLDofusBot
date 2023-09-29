package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.fight

import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffectGlobalType
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.game.fight.ai.FightState
import fr.lewon.dofus.bot.game.fight.ai.complements.AIComplement
import fr.lewon.dofus.bot.game.fight.ai.impl.DefaultFightAI
import fr.lewon.dofus.bot.game.fight.operations.FightOperation
import fr.lewon.dofus.bot.game.fight.operations.FightOperationType
import fr.lewon.dofus.bot.util.io.WaitUtil

class FightChestAI(dofusBoard: DofusBoard, aiComplement: AIComplement) : DefaultFightAI(dofusBoard, aiComplement) {

    private companion object {

        private const val TO_HIT_CHEST_BONES_ID = 2672
    }

    override fun selectStartCell(fightBoard: FightBoard): DofusCell? = dofusBoard.startCells.firstOrNull {
        it.neighbors.size == 4 && it.neighbors.all { cell -> cell.isAccessible() && fightBoard.getFighter(cell) == null }
    }

    override fun doGetNextOperation(fightBoard: FightBoard, initialState: FightState): FightOperation {
        val playerFighter = fightBoard.getPlayerFighter()
            ?: error("Player fighter not found")
        WaitUtil.waitUntil(5000) { getMonsterToHit(fightBoard) != null }
        val toHitMonster = getMonsterToHit(fightBoard)
            ?: error("Couldn't find the monster to hit. Enemies IDs : ${fightBoard.getEnemyFighters().map { it.id }}")
        val hitOperations = initialState.getPossibleOperations().filter {
            it.targetCellId == toHitMonster.cell.cellId
                && it.type == FightOperationType.SPELL
                && it.spell != null
                && isSingleAttackSpell(it.spell, playerFighter, toHitMonster)
        }
        var chosenOperations = hitOperations.filter { it.targetCellId == toHitMonster.cell.cellId }
        if (chosenOperations.isEmpty()) {
            chosenOperations = hitOperations.filter {
                val fighter = it.targetCellId?.let { cellId -> fightBoard.getFighter(cellId) }
                fighter != null && fighter.teamId != playerFighter.teamId && fighter.isSummon()
            }
        }
        return chosenOperations.minByOrNull { it.spell?.apCost ?: Int.MAX_VALUE }
            ?: FightOperation(FightOperationType.PASS_TURN)
    }

    private fun getMonsterToHit(fightBoard: FightBoard): Fighter? =
        fightBoard.getEnemyFighters().firstOrNull { it.bonesId == TO_HIT_CHEST_BONES_ID }
            ?.takeIf { fightBoard.getFighterById(it.getSummonerId()) != null }

    private fun isSingleAttackSpell(spell: DofusSpellLevel, playerFighter: Fighter, toHitMonster: Fighter): Boolean =
        spell.effects.count {
            it.effectType.globalType == DofusSpellEffectGlobalType.ATTACK
                && it.targets.any { target -> target.canHitTarget(playerFighter, toHitMonster) }
        } == 1

}
