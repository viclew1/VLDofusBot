package fr.lewon.dofus.bot.gui2.main.scripts.characters.edit.global

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.core.d2o.managers.characteristic.BreedManager
import fr.lewon.dofus.bot.core.model.charac.DofusBreed
import fr.lewon.dofus.bot.gui2.custom.ComboBox
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharacterUIState
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager

@Composable
fun CharacterGlobalInformationEditionContent(characterUIState: CharacterUIState) {
    val character = CharacterManager.getCharacter(characterUIState.name)
        ?: error("Character not found : ${characterUIState.name}")
    val breed = BreedManager.getBreed(characterUIState.dofusClassId)
    Column(Modifier.padding(5.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            CommonText("Class")
            Spacer(Modifier.widthIn(10.dp))
            ComboBox(
                selectedItem = breed,
                items = BreedManager.getAllBreeds(),
                onItemSelect = { updateBreed(character, it) },
                getItemText = { it.name }
            )
        }
    }
}

private fun updateBreed(character: DofusCharacter, breed: DofusBreed) {
    CharacterManager.updateCharacter(character.name, dofusClassId = breed.id)
}
