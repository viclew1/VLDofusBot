package fr.lewon.dofus.bot.gui.main.scripts.characters.edit.global

import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        CharacterSwitchLine("Otomai Transporter", characterUIState.isOtomaiTransportAvailable) {
            CharacterManager.updateCharacter(characterUIState.name, isOtomaiTransportAvailable = it)
        }
        CharacterSwitchLine("Frigost 2 Zaap", characterUIState.isFrigost2Available) {
            CharacterManager.updateCharacter(characterUIState.name, isFrigost2Available = it)
        }
    }
}

@Composable
private fun CharacterSwitchLine(title: String, value: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(start = 5.dp).padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonText(title, Modifier.fillMaxWidth().weight(1f), fontSize = 12.sp)
        Switch(
            value,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.height(20.dp)
        )
    }
}