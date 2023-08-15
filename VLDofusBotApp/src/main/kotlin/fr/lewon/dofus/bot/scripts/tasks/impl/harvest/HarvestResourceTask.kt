package fr.lewon.dofus.bot.scripts.tasks.impl.harvest

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightTask
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.GameContextDestroyMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.interactive.InteractiveUseErrorMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items.ObjectAddedMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items.ObjectQuantityMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.InteractiveElement
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.game.RetryUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

class HarvestResourceTask(private val interactiveElement: InteractiveElement) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        gameInfo.eventStore.clear()
        return RetryUtil.tryUntilSuccess({
            gameInfo.eventStore.getLastEvent(InteractiveUseErrorMessage::class.java) != null
                || harvestResource(logItem, gameInfo)
        }, 4)
    }

    private fun harvestResource(logItem: LogItem, gameInfo: GameInfo): Boolean {
        gameInfo.eventStore.clear()
        InteractiveUtil.useInteractive(
            gameInfo = gameInfo,
            elementId = interactiveElement.elementId,
            skillId = interactiveElement.enabledSkills.first().skillId
        )

        WaitUtil.waitUntil(8000) {
            gameInfo.eventStore.getLastEvent(ObjectQuantityMessage::class.java) != null ||
                gameInfo.eventStore.getLastEvent(ObjectAddedMessage::class.java) != null ||
                gameInfo.eventStore.getLastEvent(GameContextDestroyMessage::class.java) != null ||
                gameInfo.eventStore.getLastEvent(InteractiveUseErrorMessage::class.java) != null
        }

        WaitUtil.sleep(100)

        if (gameInfo.eventStore.getLastEvent(GameContextDestroyMessage::class.java) != null) {
            return FightTask().run(logItem, gameInfo)
        }
        return gameInfo.eventStore.getLastEvent(ObjectQuantityMessage::class.java) != null ||
            gameInfo.eventStore.getLastEvent(ObjectAddedMessage::class.java) != null
    }

    override fun onStarted(): String {
        return "Harvesting resource ..."
    }
}