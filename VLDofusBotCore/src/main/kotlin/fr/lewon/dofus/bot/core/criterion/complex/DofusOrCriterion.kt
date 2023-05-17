package fr.lewon.dofus.bot.core.criterion.complex

import fr.lewon.dofus.bot.core.criterion.DofusCriterion
import fr.lewon.dofus.bot.core.model.charac.DofusCharacterBasicInfo

class DofusOrCriterion(private val left: DofusCriterion, private val right: DofusCriterion) : DofusCriterion() {
    override fun check(characterInfo: DofusCharacterBasicInfo): Boolean {
        return left.check(characterInfo) || right.check(characterInfo)
    }
}