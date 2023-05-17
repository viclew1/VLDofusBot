package fr.lewon.dofus.bot.core.d2o.managers.interactive

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.model.interactive.DofusSkill

object SkillManager : VldbManager {

    private lateinit var skillById: Map<Double, DofusSkill>

    override fun initManager() {
        skillById = D2OUtil.getObjects("Skills").associate {
            val id = it["id"].toString().toDouble()
            val elementActionId = it["elementActionId"].toString().toInt()
            val nameId = it["nameId"].toString().toInt()
            val label = I18NUtil.getLabel(nameId) ?: "UNKNOWN_SKILL_LABEL"
            id to DofusSkill(id, elementActionId, label)
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return emptyList()
    }

    fun getSkill(skillId: Double): DofusSkill? {
        return skillById[skillId]
    }

}