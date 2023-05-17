package fr.lewon.dofus.bot.core.criterion.simple

import fr.lewon.dofus.bot.core.criterion.DofusCriterion
import fr.lewon.dofus.bot.core.model.charac.DofusCharacterBasicInfo

class DofusQuestFinishedCriterion(private val expectedValue: Int) : DofusCriterion() {
    override fun check(characterInfo: DofusCharacterBasicInfo): Boolean {
        return characterInfo.finishedQuestsIds.contains(expectedValue)
    }
}