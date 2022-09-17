package fr.lewon.dofus.bot.gui2.main.scripts.characters

import androidx.compose.ui.graphics.Color
import fr.lewon.dofus.bot.gui2.util.AppColors
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.script.ScriptRunner

enum class CharacterActivityState(
    val labelBuilder: (DofusCharacter) -> String,
    val displayOrder: Int,
    val color: Color
) {
    BUSY({ "Script running : ${ScriptRunner.getRunningScript(it)?.scriptBuilder?.name}" }, 0, AppColors.RED),
    AVAILABLE({ "Available" }, 0, AppColors.GREEN),
    TO_INITIALIZE({ "To initialize - move to another map or run any script" }, 0, AppColors.ORANGE),
    DISCONNECTED({ "Disconnected" }, 1, AppColors.DARK_BG_COLOR)
}