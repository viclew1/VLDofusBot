package fr.lewon.dofus.bot.gui.main.exploration.lastexploration.impl

import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.main.exploration.lastexploration.ExplorationProgress
import fr.lewon.dofus.bot.gui.main.exploration.lastexploration.LastExploration
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.impl.ExploreMapsScriptBuilder

class LastSubAreasExploration(progressBySubArea: Map<DofusSubArea, ExplorationProgress>) :
    LastExploration<DofusSubArea>(progressBySubArea) {

    override fun copy(
        progressByItem: Map<DofusSubArea, ExplorationProgress>,
        explorationStopped: Boolean
    ): LastExploration<DofusSubArea> =
        LastSubAreasExploration(progressByItem).also { it.explorationStopped = explorationStopped }

    override fun getLabel(item: DofusSubArea): String = item.name

    override fun updateParameterValues(parameterValues: ParameterValues) {
        parameterValues.updateParamValue(
            ExploreMapsScriptBuilder.explorationTypeParameter,
            ExploreMapsScriptBuilder.ExplorationType.SubArea
        )
        parameterValues.updateParamValue(ExploreMapsScriptBuilder.subAreasParameter, getItemsToExploreAgain())
    }
}