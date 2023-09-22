package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.gui.main.exploration.lastexploration.LastExplorationUiUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.util.ExplorationParameters
import fr.lewon.dofus.bot.util.network.info.GameInfo

abstract class MultipleExplorationTask<T>(
    private val itemsToExplore: List<T>,
    private val runForever: Boolean,
    private val explorationParameters: ExplorationParameters
) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        do {
            onExplorationStart(gameInfo.character, itemsToExplore)
            for (itemToExplore in itemsToExplore) {
                when (exploreItem(logItem, gameInfo, itemToExplore, explorationParameters)) {
                    ExplorationStatus.FoundSomething ->
                        return true
                    ExplorationStatus.NotFinished ->
                        return false
                    ExplorationStatus.Finished ->
                        LastExplorationUiUtil.updateExplorationProgress(gameInfo.character, itemToExplore, 1, 1)
                }
            }
        } while (runForever)
        return true
    }

    protected abstract fun onExplorationStart(
        character: DofusCharacter,
        itemsToExplore: List<T>
    )

    protected abstract fun exploreItem(
        logItem: LogItem,
        gameInfo: GameInfo,
        item: T,
        explorationParameters: ExplorationParameters
    ): ExplorationStatus

    protected abstract fun buildOnStartedMessage(itemsToExplore: List<T>): String

    override fun onStarted(): String = buildOnStartedMessage(itemsToExplore)
}