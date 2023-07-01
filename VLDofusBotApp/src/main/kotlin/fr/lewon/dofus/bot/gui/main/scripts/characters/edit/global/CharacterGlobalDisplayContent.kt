package fr.lewon.dofus.bot.gui.main.scripts.characters.edit.global

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharacterUIState
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

@Composable
fun CharacterGlobalDisplayContent(characterUIState: CharacterUIState) {
    Column(Modifier.padding(start = 5.dp, top = 5.dp, end = 5.dp).fillMaxHeight().width(180.dp).grayBoxStyle()) {
        CharacterNameDisplay(characterUIState)
        CharacterSkinDisplay(characterUIState)
        CharacterGlobalInformationEditionContent(characterUIState)
        HorizontalSeparator()
        CharacterCharacteristicInformationContent(characterUIState)
    }
}

@Composable
private fun CharacterNameDisplay(characterUIState: CharacterUIState) {
    Box(Modifier.fillMaxWidth().height(30.dp).darkGrayBoxStyle()) {
        SubTitleText(
            characterUIState.name,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().align(Alignment.Center)
        )
    }
}

@Composable
fun CharacterCharacteristicInformationContent(characterUIState: CharacterUIState) {
    val character = CharacterManager.getCharacter(characterUIState.name)
        ?: error("Character not found : ${characterUIState.name}")
    val connection = GameSnifferUtil.getFirstConnection(character)
    if (connection == null) {
        Row(Modifier.fillMaxSize()) {
            CommonText(
                "Characteristics unavailable, character disconnected.",
                modifier = Modifier.align(Alignment.CenterVertically),
                textAlign = TextAlign.Center
            )
        }
    } else {
        val characterGlobalInformationUIState =
            CharacterGlobalInformationUIUtil.getCharacterGlobalInformationUIState(characterUIState.name)
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.align(Alignment.Center)) {
                Row(Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.size(20.dp)) {
                        TooltipTarget("Level", 20.dp, modifier = Modifier.fillMaxSize()) {
                            Image(UiResource.LEVEL.imagePainter, "")
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    CommonText(characterGlobalInformationUIState.value.levelText, fontSize = 11.sp)
                }
                Row(Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.size(20.dp)) {
                        TooltipTarget("Kamas", 20.dp, modifier = Modifier.fillMaxSize()) {
                            Image(UiResource.KAMAS.imagePainter, "")
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    CommonText(characterGlobalInformationUIState.value.kamasText, fontSize = 11.sp)
                }
                Row(Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.size(20.dp)) {
                        TooltipTarget("Weight", 20.dp, modifier = Modifier.fillMaxSize()) {
                            Image(UiResource.WEIGHT.imagePainter, "")
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    CommonText(characterGlobalInformationUIState.value.weightText, fontSize = 11.sp)
                }
            }
        }
    }
}
