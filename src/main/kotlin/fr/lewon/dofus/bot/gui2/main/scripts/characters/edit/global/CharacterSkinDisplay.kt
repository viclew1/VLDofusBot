package fr.lewon.dofus.bot.gui2.main.scripts.characters.edit.global

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharacterUIState
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager

@Composable
fun CharacterSkinDisplay(characterUIState: CharacterUIState) {
    Box(Modifier.height(200.dp).padding(5.dp)) {
        val skinImage = characterUIState.skinImage
        Column(Modifier.align(Alignment.BottomCenter)) {
            if (skinImage == null) {
                CommonText("Initialize character to load its skin", textAlign = TextAlign.Center)
            }
            Image(BreedAssetManager.getAssets(characterUIState.dofusClassId).blurredIconPainter, "")
        }
        if (skinImage != null) {
            Image(skinImage.toPainter(), "", Modifier.align(Alignment.Center))
        }
    }
}
