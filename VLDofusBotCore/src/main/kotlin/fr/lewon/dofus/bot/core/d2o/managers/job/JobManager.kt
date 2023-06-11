package fr.lewon.dofus.bot.core.d2o.managers.job

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.model.job.DofusJob

object JobManager : VldbManager {

    private lateinit var jobById: Map<Int, DofusJob>

    override fun initManager() {
        jobById = D2OUtil.getObjects("Jobs").associate {
            val id = it["id"].toString().toInt()
            val nameId = it["nameId"].toString().toInt()
            val name = I18NUtil.getLabel(nameId) ?: "UNKNOWN_SKILL_LABEL"
            val iconId = it["iconId"].toString().toInt()
            id to DofusJob(id, name, iconId)
        }
    }

    override fun getNeededManagers(): List<VldbManager> = emptyList()

    fun getJob(jobId: Int) = jobById[jobId]

    fun getJobs() = jobById.values.toList()

}