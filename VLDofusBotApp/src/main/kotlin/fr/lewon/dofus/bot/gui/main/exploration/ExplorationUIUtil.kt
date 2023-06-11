package fr.lewon.dofus.bot.gui.main.exploration

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.gui.main.exploration.map.helper.HiddenWorldMapHelper
import fr.lewon.dofus.bot.gui.main.exploration.map.helper.MainWorldMapHelper
import fr.lewon.dofus.bot.gui.main.exploration.map.subarea.SubAreaContentTabs
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.scripts.impl.ExploreAreaScriptBuilder
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterManagerListener
import fr.lewon.dofus.bot.util.script.DofusBotScriptEndType
import fr.lewon.dofus.bot.util.script.ScriptRunner
import fr.lewon.dofus.bot.util.script.ScriptRunnerListener
import java.util.concurrent.locks.ReentrantLock

object ExplorationUIUtil : ComposeUIUtil(), ScriptRunnerListener, CharacterManagerListener {

    const val MIN_ZOOM = 1f
    const val MAX_ZOOM = 4f
    const val CELL_SIZE = 5f

    val minPosX = MapManager.getAllMaps().minOf { it.posX } - 10
    val minPosY = MapManager.getAllMaps().minOf { it.posY } - 10
    val maxPosX = MapManager.getAllMaps().maxOf { it.posX } + 10
    val maxPosY = MapManager.getAllMaps().maxOf { it.posY } + 10
    val centerX = minPosX + (maxPosX - minPosX) / 2
    val centerY = minPosY + (maxPosY - minPosY) / 2
    val totalWidth = (maxPosX - minPosX + 1) * CELL_SIZE
    val totalHeight = (maxPosY - minPosY + 1) * CELL_SIZE

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

    fun addAvailableCharacter(characterName: String) = lock.executeSyncOperation {
        explorerUIState.value = explorerUIState.value.copy(
            availableCharacters = explorerUIState.value.availableCharacters.plus(characterName),
            selectedCharacterName = if (explorerUIState.value.selectedCharacterName.isNullOrEmpty()) {
                characterName
            } else {
                explorerUIState.value.selectedCharacterName
            },
        )
    }

    fun removeAvailableCharacter(characterName: String) = lock.executeSyncOperation {
        val newAvailable = explorerUIState.value.availableCharacters.minus(characterName)
        explorerUIState.value = explorerUIState.value.copy(
            selectedCharacterName = if (characterName == explorerUIState.value.selectedCharacterName) {
                newAvailable.firstOrNull()
            } else explorerUIState.value.selectedCharacterName,
            availableCharacters = newAvailable
        )
    }

    fun requestMapUpdate() {
        mapUpdated.value = true
    }

    fun startExploration(subArea: DofusSubArea) = explorerUIState.value.selectedCharacterName?.let { characterName ->
        CharacterManager.getCharacter(characterName)?.let { character ->
            val scriptValues = ScriptValues()
            explorerUIState.value.explorationParameterValuesByParameter.forEach {
                scriptValues.updateParamValue(it.key, it.value)
            }
            scriptValues.updateParamValue(
                ExploreAreaScriptBuilder.subAreaParameter,
                subArea.label
            )
            ScriptRunner.runScript(character, ExploreAreaScriptBuilder, scriptValues)
        }
    }

    fun onAreaExplorationStart(character: DofusCharacter, subArea: DofusSubArea) = lock.executeSyncOperation {
        val mapUiStateValue = mapUIState.value
        mapUIState.value = mapUiStateValue.copy(
            areaExploredByCharacter = mapUiStateValue.areaExploredByCharacter.plus(character to subArea)
        )
    }

    override fun onScriptEnd(character: DofusCharacter, endType: DofusBotScriptEndType) = lock.executeSyncOperation {
        val mapUiStateValue = mapUIState.value
        mapUIState.value = mapUiStateValue.copy(
            areaExploredByCharacter = mapUiStateValue.areaExploredByCharacter.minus(character)
        )
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