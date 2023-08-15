package fr.lewon.dofus.bot.gui.main.scripts.character

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.ComboBox
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.SubTitleText
import fr.lewon.dofus.bot.gui.custom.darkGrayBoxStyle
import fr.lewon.dofus.bot.gui.main.characters.CharacterStateIndicator
import fr.lewon.dofus.bot.gui.main.characters.CharacterUIState
import fr.lewon.dofus.bot.gui.main.characters.edit.sets.CharacterSetsUiUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.model.characters.sets.CharacterSet
import fr.lewon.dofus.bot.model.characters.sets.CharacterSets
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterSetsManager
import fr.lewon.dofus.bot.util.filemanagers.impl.HarvestableSetsManager

@Composable
fun SelectedCharacterDisplayContent(characterUIState: CharacterUIState) {
    val characterSets = CharacterSetsUiUtil.getUiStateValue().setsByCharacterName[characterUIState.name]
        ?: CharacterSets(characterUIState.name)
    Row(Modifier.darkGrayBoxStyle().height(60.dp)) {
        CharacterStateIndicator(characterUIState)
        Row {
            Row(Modifier.height(30.dp).width(200.dp).align(Alignment.CenterVertically)) {
                val breedAssets = BreedAssetManager.getAssets(characterUIState.dofusClassId)
                Image(
                    painter = breedAssets.simpleIconPainter,
                    "",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(30.dp).align(Alignment.CenterVertically)
                )
                Spacer(Modifier.width(10.dp))
                SubTitleText(
                    characterUIState.name,
                    Modifier.align(Alignment.CenterVertically),
                    maxLines = 1,
                )
            }
            Column(Modifier.padding(4.dp)) {
                Row(Modifier.fillMaxHeight().weight(1f)) {
                    Image(
                        painter = UiResource.SPELLS.imagePainter,
                        "",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier.height(22.dp).align(Alignment.CenterVertically)
                    )
                    Spacer(Modifier.width(5.dp))
                    CommonText("Selected set :", modifier = Modifier.align(Alignment.CenterVertically).width(125.dp))
                    Row {
                        ComboBox(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            selectedItem = characterSets.getSelectedSet() ?: CharacterSet(),
                            items = characterSets.sets,
                            onItemSelect = { CharacterSetsManager.setSelectedSet(characterUIState.name, it.name) },
                            getItemText = { it.name },
                            colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.DARK_BG_COLOR)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxHeight().weight(1f)) {
                    Image(
                        painter = UiResource.JOBS.imagePainter,
                        "",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier.height(22.dp).align(Alignment.CenterVertically)
                    )
                    Spacer(Modifier.width(5.dp))
                    CommonText("Harvestable set :", modifier = Modifier.align(Alignment.CenterVertically).width(125.dp))
                    Row {
                        ComboBox(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            selectedItem = characterUIState.parameters.harvestableSet,
                            items = HarvestableSetsManager.getHarvestableIdsBySetName().keys.toList(),
                            onItemSelect = {
                                CharacterManager.updateCharacter(
                                    name = characterUIState.name,
                                    characterParameters = characterUIState.parameters.copy(harvestableSet = it)
                                )
                            },
                            getItemText = { it },
                            colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.DARK_BG_COLOR)
                        )
                    }
                }
            }
        }
    }
}