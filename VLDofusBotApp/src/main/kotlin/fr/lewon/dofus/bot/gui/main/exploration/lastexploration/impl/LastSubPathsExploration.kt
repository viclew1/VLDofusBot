package fr.lewon.dofus.bot.gui.main.exploration.lastexploration.impl

import fr.lewon.dofus.bot.gui.main.exploration.lastexploration.ExplorationProgress
import fr.lewon.dofus.bot.gui.main.exploration.lastexploration.LastExploration
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.model.characters.paths.SubPath
import fr.lewon.dofus.bot.scripts.impl.ExploreMapsScriptBuilder
import fr.lewon.dofus.bot.util.filemanagers.impl.MapsPathsManager

class LastSubPathsExploration(progressBySubPath: Map<SubPath, ExplorationProgress>) :
    LastExploration<SubPath>(progressBySubPath) {

    override fun copy(
        progressByItem: Map<SubPath, ExplorationProgress>,
        explorationStopped: Boolean
    ): LastExploration<SubPath> =
        LastSubPathsExploration(progressByItem).also { it.explorationStopped = explorationStopped }

    override fun getLabel(item: SubPath): String = item.displayName

    override fun updateParameterValues(parameterValues: ParameterValues) {
        parameterValues.updateParamValue(
            ExploreMapsScriptBuilder.explorationTypeParameter,
            ExploreMapsScriptBuilder.ExplorationType.Path
        )
        val pathName = getItemsToExploreAgain().firstOrNull()?.pathName
        if (pathName != null) {
            parameterValues.updateParamValue(
                ExploreMapsScriptBuilder.pathParameter,
                MapsPathsManager.getPathByName()[pathName]
            )
        }
    }
}