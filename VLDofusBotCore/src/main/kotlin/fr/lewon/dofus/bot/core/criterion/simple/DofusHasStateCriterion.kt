package fr.lewon.dofus.bot.core.criterion.simple

import fr.lewon.dofus.bot.core.criterion.DofusCriterion
import fr.lewon.dofus.bot.core.criterion.parse.CriterionOperator
import fr.lewon.dofus.bot.core.model.charac.DofusCharacterBasicInfo

class DofusHasStateCriterion(
    private val operator: CriterionOperator,
    private val expectedValue: Int
) : DofusCriterion() {
    override fun check(characterInfo: DofusCharacterBasicInfo): Boolean {
        return when (operator) {
            CriterionOperator.EQUALS -> characterInfo.states.contains(expectedValue)
            CriterionOperator.IS_DIFFERENT -> !characterInfo.states.contains(expectedValue)
            else -> false
        }
    }
}