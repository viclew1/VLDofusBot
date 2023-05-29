package fr.lewon.dofus.bot.scripts.tasks.impl.harvest

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.harvest.JobSkillsManager
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightTask
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.GameEntitiesDispositionMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items.ObjectQuantityMessage
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.game.RetryUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

class HarvestResourceTask(
    private val harvestJob : String,
    private val stopIfNoResourcePresent: Boolean = false
) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        gameInfo.eventStore.clear()
        val couldHarvest = RetryUtil.tryUntilSuccess({ harvestAnyResource(logItem, gameInfo, harvestJob) }, 20, {
            if (stopIfNoResourcePresent && gameInfo.interactiveElements.any { it.enabledSkills.isEmpty() }) {
                error("No resource on map")
            }
            WaitUtil.waitUntil({ gameInfo.interactiveElements.any { it.enabledSkills.isNotEmpty() } }, 60000)
        })
        if (!couldHarvest) {
            error("Couldn't find or collect a resource on map")
        }

        return true
    }

    private fun harvestAnyResource(logItem: LogItem, gameInfo: GameInfo, harvestJob: String): Boolean {
        val skillList = JobSkillsManager()

        val interactive = gameInfo.interactiveElements
            .filter { interactive ->
                interactive.enabledSkills.isNotEmpty() &&
                        interactive.enabledSkills.any { skill ->
                            skillList.checkSkillExists(harvestJob, skill.skillId)
                        }
            }
            .firstOrNull()

        if (interactive != null) {
            InteractiveUtil.useInteractive(
                gameInfo = gameInfo,
                elementId = interactive.elementId,
                skillId = interactive.enabledSkills.first().skillId
            )

            WaitUtil.waitUntil(
                {
                    gameInfo.eventStore.getLastEvent(ObjectQuantityMessage::class.java) != null ||
                            gameInfo.eventStore.getLastEvent(GameEntitiesDispositionMessage::class.java) != null
                }, 8000
            )

            if (gameInfo.eventStore.getLastEvent(GameEntitiesDispositionMessage::class.java) != null) {
                return FightTask().run(logItem, gameInfo)
            }
            if (gameInfo.eventStore.getLastEvent(ObjectQuantityMessage::class.java) != null) {
                return true
            }
        }

        return false
    }


    override fun onStarted(): String {
        return "Collecting any resource ... "
    }
}