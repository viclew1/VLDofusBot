package fr.lewon.dofus.bot.gui.main.characters

import androidx.compose.ui.graphics.painter.Painter
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.model.characters.DofusCharacterParameters
import fr.lewon.dofus.bot.util.script.ScriptRunner

data class CharacterUIState(
    val name: String,
    val dofusClassId: Int,
    val parameters: DofusCharacterParameters,
    val currentMap: DofusMap? = null,
    val activityState: CharacterActivityState = CharacterActivityState.DISCONNECTED,
    val runningScript: ScriptRunner.RunningScript? = null,
    val flashVars: String? = null,
    val skinImage: Painter? = null,
    val skinImageState: SkinImageState = SkinImageState.NOT_LOADED,
    val currentHintName: String? = null,
)

enum class SkinImageState {
    NOT_LOADED,
    LOADING,
    BROKEN,
    LOADED
}