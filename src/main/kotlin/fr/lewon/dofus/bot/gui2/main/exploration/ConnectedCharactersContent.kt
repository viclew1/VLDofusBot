package fr.lewon.dofus.bot.gui2.main.exploration.map.helper

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharacterActivityState
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharacterUIState
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager

@Composable
fun ConnectedCharactersContent() {
    Column(Modifier.width(200.dp).padding(5.dp).fillMaxHeight()) {
        CommonText("Connected characters", Modifier.padding(bottom = 10.dp), fontWeight = FontWeight.Bold)
        val connectedCharactersUIStates = CharactersUIUtil.getAllCharacterUIStates().map { it.value }
            .filter { it.activityState != CharacterActivityState.DISCONNECTED }
        if (connectedCharactersUIStates.isEmpty()) {
            CommonText("No connected character", Modifier.padding(5.dp))
        } else {
            connectedCharactersUIStates.sortedBy {
                if (it.activityState == CharacterActivityState.BUSY) 1 else 0
            }.forEach { uiState ->
                ConnectedCharacterContent(uiState)
            }
        }
    }
}

@Composable
private fun ConnectedCharacterContent(characterUIState: CharacterUIState) {
    Row(modifier = Modifier.height(25.dp).border(BorderStroke(1.dp, Color.Black))) {
        Row(Modifier.width(6.dp).fillMaxHeight().background(characterUIState.activityState.color)) { }
        Box(Modifier.fillMaxSize().background(Color.DarkGray)) {
            val breedAssets = BreedAssetManager.getAssets(characterUIState.dofusClassId)
            Row(Modifier.fillMaxWidth()) {
                Image(
                    painter = breedAssets.simpleIconPainter,
                    "",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.padding(end = 10.dp).align(Alignment.CenterVertically)
                )
                CommonText(
                    characterUIState.name,
                    Modifier.align(Alignment.CenterVertically),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
