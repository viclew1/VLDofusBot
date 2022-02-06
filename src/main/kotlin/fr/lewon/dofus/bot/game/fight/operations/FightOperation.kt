package fr.lewon.dofus.bot.game.fight.operations

import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.fight.ai.mcts.MctsMove

data class FightOperation(
    val type: FightOperationType,
    val targetCellId: Int? = null,
    val spell: DofusSpellLevel? = null,
    val dist: Int? = null
) : MctsMove {
    override fun compareTo(other: MctsMove?): Int {
        error("Non supported")
    }

    override fun toString(): String {
        var str = "[$type] - targetCellId : $targetCellId"
        if (dist != null) {
            str += " - dist : $dist"
        }
        if (spell != null) {
            str += " - spell : $spell"
        }
        return str
    }
}