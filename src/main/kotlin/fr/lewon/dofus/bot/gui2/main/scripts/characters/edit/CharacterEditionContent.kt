package fr.lewon.dofus.bot.gui2.main.scripts.characters.edit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.util.AppColors

@Composable
fun CharacterEditionContent() {
    Column(
        Modifier.fillMaxSize().padding(start = 5.dp, end = 5.dp, bottom = 5.dp)
            .border(BorderStroke(1.dp, Color.LightGray))
            .background(AppColors.DARK_BG_COLOR)
    ) {
    }
}