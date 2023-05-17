package fr.lewon.dofus.bot.model.criterion

import fr.lewon.dofus.bot.core.criterion.DofusCriterion

abstract class DofusCustomCriterion : DofusCriterion() {

    fun generateKey(): String = this::class.java.simpleName

}