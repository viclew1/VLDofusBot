package fr.lewon.dofus.bot.gui.main.exploration

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.gui.main.exploration.lastexploration.LastExplorationUiUtil
import fr.lewon.dofus.bot.gui.main.exploration.map.helper.HiddenWorldMapHelper
import fr.lewon.dofus.bot.gui.main.exploration.map.helper.MainWorldMapHelper
import fr.lewon.dofus.bot.gui.main.exploration.map.subarea.SubAreaContentTabs
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.impl.ExploreAreaScriptBuilder
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterManagerListener
import fr.lewon.dofus.bot.util.script.DofusBotScriptEndType
import fr.lewon.dofus.bot.util.script.ScriptRunner
import fr.lewon.dofus.bot.util.script.ScriptRunnerListener
import java.util.concurrent.locks.ReentrantLock

object ExplorationUIUtil : ComposeUIUtil(), ScriptRunnerListener, CharacterManagerListener {

    const val MinAreasToExplore = 5

    const val CellSize = 5f

    private const val deltaPos = 5
    private val validMaps = MapManager.getAllMaps().filter { it.subArea.area.id != 56.0 }
    val minPosX = validMaps.minOf { it.posX } - deltaPos
    val minPosY = validMaps.minOf { it.posY } - deltaPos
    val maxPosX = validMaps.maxOf { it.posX } + deltaPos
    val maxPosY = validMaps.maxOf { it.posY } + deltaPos
    val totalWidth = (maxPosX - minPosX + 1) * CellSize
    val totalHeight = (maxPosY - minPosY + 1) * CellSize

    val ExplorerParameters = listOf(
        ExploreAreaScriptBuilder.stopWhenArchMonsterFoundParameter,
        ExploreAreaScriptBuilder.stopWhenQuestMonsterFoundParameter,
        ExploreAreaScriptBuilder.searchedMonsterParameter,
        ExploreAreaScriptBuilder.killEverythingParameter,
        ExploreAreaScriptBuilder.maxMonsterGroupLevelParameter,
        ExploreAreaScriptBuilder.maxMonsterGroupSizeParameter,
        ExploreAreaScriptBuilder.runForeverParameter,
        ExploreAreaScriptBuilder.ignoreMapsExploredRecentlyParameter,
        ExploreAreaScriptBuilder.useZaapsParameter,
    )

    val mapUIState = mutableStateOf(ExplorationMapUIState())
    val explorerUIState = mutableStateOf(ExplorationExplorerUIState())
    val worldMapHelpers = listOf(MainWorldMapHelper, HiddenWorldMapHelper)
    val worldMapHelper = mutableStateOf(worldMapHelpers.first())
    val mapUpdated = mutableStateOf(true)
    val currentSubAreaContentTab = mutableStateOf(SubAreaContentTabs.MONSTERS)

    private val lock = ReentrantLock()

    init {
        ScriptRunner.removeListener(this)
        for (character in CharacterManager.getCharacters()) {
            ScriptRunner.addListener(character, this)
        }
    }

    fun requestMapUpdate() {
        mapUpdated.value = true
    }

    fun startExploration(subAreas: List<DofusSubArea>, characterName: String) {
        val parameterValues = buildParameterValues(subAreas)
        CharacterManager.getCharacter(characterName)?.let { character ->
            ScriptRunner.runScript(character, ExploreAreaScriptBuilder, parameterValues)
        }
    }

    fun buildParameterValues(subAreas: List<DofusSubArea>): ParameterValues {
        val parameterValues = explorerUIState.value.explorationParameterValues.deepCopy()
        parameterValues.updateParamValue(ExploreAreaScriptBuilder.subAreasParameter, subAreas)
        return parameterValues
    }

    fun onAreaExplorationStart(character: DofusCharacter, subArea: DofusSubArea) = lock.executeSyncOperation {
        val mapUiStateValue = mapUIState.value
        mapUIState.value = mapUiStateValue.copy(
            areaExploredByCharacter = mapUiStateValue.areaExploredByCharacter.plus(character to subArea)
        )
    }

    private fun onExplorationStop(character: DofusCharacter) = lock.executeSyncOperation {
        val mapUiStateValue = mapUIState.value
        mapUIState.value = mapUiStateValue.copy(
            areaExploredByCharacter = mapUiStateValue.areaExploredByCharacter.minus(character)
        )
        LastExplorationUiUtil.stopExploration(character)
    }

    override fun onScriptEnd(character: DofusCharacter, endType: DofusBotScriptEndType) {
        onExplorationStop(character)
    }

    override fun onScriptStart(character: DofusCharacter, script: ScriptRunner.RunningScript) {
        // Nothing
    }

    override fun onCharacterCreate(character: DofusCharacter) {
        ScriptRunner.addListener(character, this)
    }

    override fun onCharacterDelete(character: DofusCharacter) {
        // Nothing
    }

    override fun onCharacterUpdate(character: DofusCharacter) {
        // Nothing
    }
}