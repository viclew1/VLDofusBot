package fr.lewon.dofus.bot.gui2.main.scripts.characters.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.lewon.dofus.bot.gui2.main.scripts.characters.edit.global.CharacterGlobalDisplayContent
import fr.lewon.dofus.bot.gui2.util.AppColors

@Composable
fun CharacterEditionContent() {
    Row(Modifier.fillMaxSize().background(AppColors.VERY_DARK_BG_COLOR)) {
        val characterUIState = CharacterEditionUIUtil.getEditedCharacterUIState()?.value
            ?: error("An edited character should be set")
        CharacterGlobalDisplayContent(characterUIState)
        CharacterEditionTabsContent(characterUIState)
    }
}