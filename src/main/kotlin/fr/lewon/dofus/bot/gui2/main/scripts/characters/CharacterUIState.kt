package fr.lewon.dofus.bot.gui2.main.scripts.characters

import androidx.compose.ui.graphics.painter.Painter
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIUtil
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.util.script.ScriptRunner

data class CharacterUIState(
    val name: String,
    val dofusClassId: Int,
    val isOtomaiTransportAvailable: Boolean,
    val activityState: CharacterActivityState = CharacterActivityState.DISCONNECTED,
    val checked: Boolean = false,
    val runningScript: ScriptRunner.RunningScript? = null,
    val scriptBuilder: DofusBotScriptBuilder = ScriptTabsUIUtil.scripts.first(),
    val flashVars: String? = null,
    val skinImage: Painter? = null,
    val skinImageState: SkinImageState = SkinImageState.NOT_LOADED
)

enum class SkinImageState {
    NOT_LOADED, LOADING, BROKEN, LOADED
}