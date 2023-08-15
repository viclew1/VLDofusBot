package fr.lewon.dofus.bot.gui.main.characters.edit.global

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.core.d2o.managers.characteristic.BreedManager
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.gui.main.characters.CharacterUIState
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

@Composable
fun CharacterGlobalDisplayContent(characterUIState: CharacterUIState) {
    Column(Modifier.grayBoxStyle()) {
        CharacterNameDisplay(characterUIState)
        Row(Modifier.fillMaxHeight().weight(1f)) {
            CharacterSkinDisplay(characterUIState)
        }
        CharacterBreedEditionContent(characterUIState)
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
private fun CharacterBreedEditionContent(characterUIState: CharacterUIState) {
    val breed = BreedManager.getBreed(characterUIState.dofusClassId)
    Column(Modifier.padding(horizontal = 5.dp)) {
        Row(Modifier.fillMaxWidth().height(25.dp), verticalAlignment = Alignment.CenterVertically) {
            ComboBox(
                selectedItem = breed,
                items = BreedManager.getAllBreeds(),
                onItemSelect = {
                    CharacterManager.updateCharacter(characterUIState.name, dofusClassId = it.id)
                },
                getItemIconPainter = { BreedAssetManager.getAssets(it.id).simpleIcon.toPainter() },
                getItemText = { it.name }
            )
        }
    }
}

@Composable
fun CharacterCharacteristicInformationContent(characterUIState: CharacterUIState) {
    val character = CharacterManager.getCharacter(characterUIState.name)
        ?: error("Character not found : ${characterUIState.name}")
    val connection = GameSnifferUtil.getFirstConnection(character)
    val characterGlobalInformationUIState =
        CharacterGlobalInformationUIUtil.getCharacterGlobalInformationUIState(characterUIState.name)
    Box {
        if (connection == null) {
            Row(Modifier.align(Alignment.Center)) {
                CommonText(
                    "Characteristics unavailable, character disconnected.",
                    modifier = Modifier.align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
            }
        }
        Column(Modifier.alpha(if (connection == null) 0f else 1f)) {
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
