package fr.lewon.dofus.bot.gui2.main.scripts.characters

import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.script.ScriptRunner

data class CharacterUIState(
    val character: DofusCharacter,
    val activityState: CharacterActivityState = CharacterActivityState.DISCONNECTED,
    val checked: Boolean = false,
    val runningScript: ScriptRunner.RunningScript? = null,
)