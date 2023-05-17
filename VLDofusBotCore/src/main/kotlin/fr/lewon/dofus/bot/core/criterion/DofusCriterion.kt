package fr.lewon.dofus.bot.core.criterion

import fr.lewon.dofus.bot.core.criterion.complex.DofusAndCriterion
import fr.lewon.dofus.bot.core.criterion.complex.DofusOrCriterion
import fr.lewon.dofus.bot.core.model.charac.DofusCharacterBasicInfo

abstract class DofusCriterion {
    abstract fun check(characterInfo: DofusCharacterBasicInfo): Boolean

    fun or(criterion: DofusCriterion): DofusCriterion {
        return DofusOrCriterion(this, criterion)
    }

    fun and(criterion: DofusCriterion): DofusCriterion {
        return DofusAndCriterion(this, criterion)
    }

}