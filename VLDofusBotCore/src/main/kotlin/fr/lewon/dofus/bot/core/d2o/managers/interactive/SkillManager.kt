package fr.lewon.dofus.bot.core.d2o.managers.interactive

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.d2o.managers.item.ItemManager
import fr.lewon.dofus.bot.core.d2o.managers.job.JobManager
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
            val parentJobId = it["parentJobId"].toString().toInt()
            val parentJob = JobManager.getJob(parentJobId)
            val gatheredResourceItemId = it["gatheredRessourceItem"].toString().toIntOrNull()
            val gatheredResourceItem = gatheredResourceItemId?.takeIf { itemId -> itemId > 0 }?.let { itemId ->
                ItemManager.getItem(itemId.toDouble())
            }
            val levelMin = it["levelMin"].toString().toInt()
            id to DofusSkill(id, elementActionId, label, parentJob, gatheredResourceItem, levelMin)
        }
    }

    override fun getNeededManagers(): List<VldbManager> = listOf(ItemManager, JobManager)

    fun getSkill(skillId: Double) = skillById[skillId]

    fun getSkills() = skillById.values.toList()

}