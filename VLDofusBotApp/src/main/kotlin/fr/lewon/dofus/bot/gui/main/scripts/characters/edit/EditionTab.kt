package fr.lewon.dofus.bot.gui.main.scripts.characters.edit

import androidx.compose.runtime.Composable
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharacterUIState
import fr.lewon.dofus.bot.gui.main.scripts.characters.edit.chat.CharacterChatContent
import fr.lewon.dofus.bot.gui.main.scripts.characters.edit.spells.CharacterSpellsEditionContent
import fr.lewon.dofus.bot.gui.util.UiResource

enum class EditionTab(
    val title: String,
    val resource: UiResource,
    val content: @Composable (CharacterUIState) -> Unit
) {
    SPELLS("Spells", UiResource.SPELLS, { CharacterSpellsEditionContent(it) }),
    CHAT("Chat", UiResource.CHAT, { CharacterChatContent(it) }),
    ;
}