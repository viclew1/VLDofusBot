package fr.lewon.dofus.bot.gui2.main.scripts.characters

import fr.lewon.dofus.bot.gui2.util.UiResource
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.script.ScriptRunner

enum class CharacterActivityState(val labelBuilder: (DofusCharacter) -> String, val uiResource: UiResource) {
    BUSY({ "Script running : ${ScriptRunner.getRunningScript(it)?.scriptBuilder?.name}" }, UiResource.RED_CIRCLE),
    AVAILABLE({ "Available" }, UiResource.GREEN_CIRCLE),
    TO_INITIALIZE({ "To initialize - move to another map or run any script" }, UiResource.ORANGE_CIRCLE),
    DISCONNECTED({ "Disconnected" }, UiResource.BLACK_CIRCLE)
}