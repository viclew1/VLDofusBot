package fr.lewon.dofus.bot.gui.main.characters

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.SubTitleText
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager

@Composable
fun RowScope.CharacterCardContent(characterUIState: CharacterUIState, textColor: Color) {
    CharacterStateIndicator(characterUIState)
    CardMainContent(characterUIState, textColor)
}

@Composable
fun CharacterStateIndicator(characterUIState: CharacterUIState) {
    val color = characterUIState.activityState.color
    val character = CharacterManager.getCharacter(characterUIState.name)
        ?: error("Character not found : ${characterUIState.name}")
    val label = characterUIState.activityState.labelBuilder(character)
    Row(Modifier.width(6.dp).fillMaxHeight()) {
        TooltipTarget(label, 20.dp, modifier = Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxSize().background(color)) { }
        }
    }
}

@Composable
private fun RowScope.CardMainContent(characterUIState: CharacterUIState, textColor: Color) {
    val breedAssets = BreedAssetManager.getAssets(characterUIState.dofusClassId)
    Image(painter = breedAssets.simpleIconPainter, "", contentScale = ContentScale.FillHeight)
    Spacer(Modifier.width(10.dp))
    SubTitleText(
        characterUIState.name,
        Modifier.align(Alignment.CenterVertically),
        maxLines = 1,
        enabledColor = textColor
    )
}