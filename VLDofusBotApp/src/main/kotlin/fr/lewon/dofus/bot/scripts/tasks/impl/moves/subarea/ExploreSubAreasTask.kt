package fr.lewon.dofus.bot.scripts.tasks.impl.moves.subarea

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.main.exploration.lastexploration.LastExplorationUiUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.ExplorationStatus
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MultipleExplorationTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.util.ExplorationParameters
import fr.lewon.dofus.bot.util.network.info.GameInfo

class ExploreSubAreasTask(
    subAreas: List<DofusSubArea>,
    runForever: Boolean,
    explorationParameters: ExplorationParameters,
) : MultipleExplorationTask<DofusSubArea>(subAreas, runForever, explorationParameters) {

    override fun onExplorationStart(character: DofusCharacter, itemsToExplore: List<DofusSubArea>) =
        LastExplorationUiUtil.onExplorationStart(character, itemsToExplore)

    override fun exploreItem(
        logItem: LogItem,
        gameInfo: GameInfo,
        item: DofusSubArea,
        explorationParameters: ExplorationParameters
    ): ExplorationStatus =
        ExploreSubAreaTask(subArea = item, explorationParameters = explorationParameters).run(logItem, gameInfo)

    override fun buildOnStartedMessage(itemsToExplore: List<DofusSubArea>): String =
        "Exploring sub paths [${itemsToExplore.joinToString(", ") { it.label }})]"

}