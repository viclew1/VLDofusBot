package fr.lewon.dofus.bot.gui.main.scripts.characters.edit.global

import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.core.d2o.managers.characteristic.BreedManager
import fr.lewon.dofus.bot.gui.custom.ComboBox
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharacterUIState
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager

@Composable
fun CharacterGlobalInformationEditionContent(characterUIState: CharacterUIState) {
    val breed = BreedManager.getBreed(characterUIState.dofusClassId)
    Column(Modifier.padding(horizontal = 5.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            ComboBox(
                selectedItem = breed,
                items = BreedManager.getAllBreeds(),
                onItemSelect = {
                    CharacterManager.updateCharacter(characterUIState.name, dofusClassId = it.id)
                },
                getItemText = { it.name }
            )
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            CommonText("Otomai Transporter", Modifier.fillMaxWidth(0.6f))
            Spacer(Modifier.fillMaxWidth().weight(1f))
            Switch(
                characterUIState.isOtomaiTransportAvailable,
                onCheckedChange = {
                    CharacterManager.updateCharacter(characterUIState.name, isOtomaiTransportAvailable = it)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}