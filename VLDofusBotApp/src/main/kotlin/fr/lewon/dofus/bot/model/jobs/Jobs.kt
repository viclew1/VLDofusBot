package fr.lewon.dofus.bot.model.jobs

import fr.lewon.dofus.bot.core.d2o.managers.interactive.SkillManager
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.InteractiveElement

enum class Jobs(private val jobId: Int) {
    LUMBERJACK(2),
    MINER(24),
    ALCHEMIST(26),
    FARMER(28),
    FISHERMAN(36);

    val skillIds by lazy {
        SkillManager.getSkills().filter { it.parentJobId == jobId }.map { it.skillId }
    }

    companion object {
        fun shouldHarvest(interactiveElement: InteractiveElement, jobs: List<Jobs>): Boolean =
            interactiveElement.enabledSkills.any { skill ->
                jobs.any { job -> job.skillIds.contains(skill.skillId.toDouble()) }
            }
    }
}