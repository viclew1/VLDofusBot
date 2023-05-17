package fr.lewon.dofus.bot.core.criterion.simple

import fr.lewon.dofus.bot.core.criterion.DofusCriterion
import fr.lewon.dofus.bot.core.criterion.parse.CriterionOperator
import fr.lewon.dofus.bot.core.model.charac.DofusCharacterBasicInfo

class DofusQuestObjectiveCriterion(
    private val operator: CriterionOperator,
    private val expectedValue: Int
) : DofusCriterion() {
    override fun check(characterInfo: DofusCharacterBasicInfo): Boolean {
        return when (operator) {
            CriterionOperator.EQUALS -> characterInfo.activeObjectiveIds.contains(expectedValue)
            CriterionOperator.IS_DIFFERENT -> !characterInfo.activeObjectiveIds.contains(expectedValue)
            CriterionOperator.GREATER_THAN -> characterInfo.finishedObjectiveIds.contains(expectedValue)
            CriterionOperator.LOWER_THAN -> !characterInfo.finishedObjectiveIds.contains(expectedValue)
        }
    }
}