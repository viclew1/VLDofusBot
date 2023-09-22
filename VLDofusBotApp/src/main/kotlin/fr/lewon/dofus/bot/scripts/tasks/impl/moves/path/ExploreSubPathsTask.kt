package fr.lewon.dofus.bot.scripts.tasks.impl.moves.path

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.gui.main.exploration.lastexploration.LastExplorationUiUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.paths.SubPath
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.ExplorationStatus
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MultipleExplorationTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.util.ExplorationParameters
import fr.lewon.dofus.bot.util.network.info.GameInfo

class ExploreSubPathsTask(
    subPaths: List<SubPath>,
    runForever: Boolean,
    explorationParameters: ExplorationParameters,
) : MultipleExplorationTask<SubPath>(subPaths, runForever, explorationParameters) {

    override fun onExplorationStart(character: DofusCharacter, itemsToExplore: List<SubPath>) =
        LastExplorationUiUtil.onExplorationStart(character, itemsToExplore)

    override fun exploreItem(
        logItem: LogItem,
        gameInfo: GameInfo,
        item: SubPath,
        explorationParameters: ExplorationParameters
    ): ExplorationStatus =
        ExploreSubPathTask(subPath = item, explorationParameters = explorationParameters).run(logItem, gameInfo)

    override fun buildOnStartedMessage(itemsToExplore: List<SubPath>): String =
        "Exploring sub paths [${itemsToExplore.joinToString(", ") { it.displayName }})]"

}