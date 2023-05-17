package fr.lewon.dofus.bot.core.criterion.simple

import fr.lewon.dofus.bot.core.criterion.DofusCriterion
import fr.lewon.dofus.bot.core.criterion.parse.CriterionOperator
import fr.lewon.dofus.bot.core.model.charac.DofusCharacterBasicInfo

class DofusBreedCriterion(private val operator: CriterionOperator, private val expectedValue: Int) : DofusCriterion() {
    override fun check(characterInfo: DofusCharacterBasicInfo): Boolean {
        return operator.checkFunc(characterInfo.breedId, expectedValue)
    }
}