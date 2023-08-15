package fr.lewon.dofus.bot.gui.main.characters.edit.global

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.CustomShapes
import fr.lewon.dofus.bot.gui.custom.RefreshButton
import fr.lewon.dofus.bot.gui.main.characters.CharacterUIState
import fr.lewon.dofus.bot.gui.main.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.main.characters.SkinImageState
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager

@Composable
fun CharacterSkinDisplay(characterUIState: CharacterUIState) {
    Box(Modifier.fillMaxSize()) {
        Image(
            BreedAssetManager.getAssets(characterUIState.dofusClassId).blurredIconPainter,
            "",
            Modifier.align(Alignment.Center)
        )
        val skinImagePainter = characterUIState.skinImage
        if (skinImagePainter != null) {
            Image(skinImagePainter, "", Modifier.padding(5.dp).align(Alignment.Center))
        }
        val skinImageState = characterUIState.skinImageState
        val imageStateText = when (skinImageState) {
            SkinImageState.NOT_LOADED -> "Initialize character to load its skin"
            SkinImageState.BROKEN -> "Failed to load skin, Dofusbook might be unreachable"
            SkinImageState.LOADING -> "Loading skin ..."
            else -> ""
        }
        if (imageStateText.isNotBlank()) {
            CommonText(
                imageStateText,
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(5.dp).padding(top = 30.dp),
                textAlign = TextAlign.Center
            )
        }
        if (skinImageState == SkinImageState.LOADING) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(0.9f).align(Alignment.BottomCenter),
                color = AppColors.primaryColor
            )
        } else if (skinImageState == SkinImageState.BROKEN) {
            Row(Modifier.align(Alignment.BottomCenter).height(20.dp)) {
                RefreshButton(
                    { CharactersUIUtil.refreshSkin(characterUIState.name) },
                    "",
                    CustomShapes.buildTrapezoidShape(topLeftDeltaRatio = 0.15f, topRightDeltaRatio = 0.15f),
                    width = 60.dp
                )
            }
        }
    }
}
