package fr.lewon.dofus.bot.gui.main.exploration

import fr.lewon.dofus.bot.scripts.impl.ExploreAreaScriptBuilder
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

data class ExplorationExplorerUIState(
    val selectedCharacterName: String? = null,
    val explorationParameterValuesByParameter: Map<DofusBotParameter, String> = listOf(
        ExploreAreaScriptBuilder.harvestParameter,
        ExploreAreaScriptBuilder.stopWhenArchMonsterFoundParameter,
        ExploreAreaScriptBuilder.stopWhenQuestMonsterFoundParameter,
        ExploreAreaScriptBuilder.searchedMonsterParameter,
        ExploreAreaScriptBuilder.killEverythingParameter,
        ExploreAreaScriptBuilder.runForeverParameter,
        ExploreAreaScriptBuilder.ignoreMapsExploredRecentlyParameter
    ).associateWith { it.defaultValue }
)