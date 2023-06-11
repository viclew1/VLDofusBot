package fr.lewon.dofus.bot.model.jobs

import fr.lewon.dofus.bot.core.d2o.managers.interactive.SkillManager
import fr.lewon.dofus.bot.core.d2o.managers.job.JobManager

enum class HarvestJobs(private val jobId: Int) {
    LUMBERJACK(2),
    MINER(24),
    ALCHEMIST(26),
    FARMER(28),
    FISHERMAN(36);

    private val skills by lazy {
        SkillManager.getSkills().filter { it.parentJob?.id == jobId }.sortedBy { it.levelMin }
    }

    val skillIds by lazy {
        skills.map { it.skillId }
    }

    val items by lazy {
        skills.mapNotNull { it.gatheredResourceItem }.distinct()
    }

    val jobName = JobManager.getJob(jobId)?.name ?: error("Job does not exist : $jobId")

}