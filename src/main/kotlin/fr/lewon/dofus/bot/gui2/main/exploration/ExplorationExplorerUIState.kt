package fr.lewon.dofus.bot.gui2.main.exploration

import fr.lewon.dofus.bot.scripts.impl.ExploreAreaScriptBuilder
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

data class ExplorationExplorerUIState(
    val selectedCharacterName: String? = null,
    val availableCharacters: List<String> = emptyList(),
    val explorationParameterValuesByName: Map<DofusBotParameter, String> = mapOf(
        ExploreAreaScriptBuilder.stopWhenArchMonsterFoundParameter to true.toString(),
        ExploreAreaScriptBuilder.stopWhenQuestMonsterFoundParameter to false.toString(),
        ExploreAreaScriptBuilder.killEverythingParameter to false.toString(),
        ExploreAreaScriptBuilder.runForeverParameter to false.toString(),
        ExploreAreaScriptBuilder.searchedMonsterParameter to ""
    )
)