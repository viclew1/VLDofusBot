package fr.lewon.dofus.bot.model.transition.zaap

import fr.lewon.dofus.bot.core.criterion.DofusCriterion
import fr.lewon.dofus.bot.core.model.charac.DofusCharacterBasicInfo

const val ZaapCriterionKey = "ValidZaapCriterion"

class ZaapTransitionCriterion(val mapId: Double) : DofusCriterion() {
    override fun check(characterInfo: DofusCharacterBasicInfo): Boolean {
        return !characterInfo.disabledTransitionZaapMapIds.contains(mapId)
    }
}