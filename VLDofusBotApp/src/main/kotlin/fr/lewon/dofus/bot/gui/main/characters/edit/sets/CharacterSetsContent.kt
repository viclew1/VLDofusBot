package fr.lewon.dofus.bot.gui.main.characters.edit.sets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.characters.CharacterUIState
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.model.characters.sets.CharacterSet
import fr.lewon.dofus.bot.model.characters.sets.CharacterSets
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterSetsManager

@Composable
fun CharacterSetsContent(characterUIState: CharacterUIState) {
    val characterSets = CharacterSetsUiUtil.getUiStateValue().setsByCharacterName[characterUIState.name]
        ?: CharacterSets(characterUIState.name)
    val selectedSet = characterSets.getSelectedSet()
    CustomStyledColumn("Character Sets", Modifier.fillMaxSize().padding(5.dp)) {
        SetCreationLine(characterUIState, characterSets)
        for (set in characterSets.sets) {
            val selected = set == selectedSet
            Column(Modifier.height(30.dp)) {
                val isHovered = remember { mutableStateOf(false) }
                val hoverAlpha = if (isHovered.value) 1f else 0.7f
                val backgroundColor = Color.DarkGray.copy(alpha = hoverAlpha)
                Row(
                    modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).defaultHoverManager(isHovered)
                ) {
                    Box(Modifier.fillMaxSize().weight(1f).background(backgroundColor)) {
                        AnimatedBackgroundSelectedColor(selected)
                        CardMainContent(characterUIState.name, set.name, selected)
                        val isDeletable = set.name != CharacterSetsManager.DefaultSetName
                        HoverButtons(characterUIState.name, set.name, isHovered.value, isDeletable)
                    }
                }
            }
        }
    }
}

@Composable
private fun SetCreationLine(characterUIState: CharacterUIState, characterSets: CharacterSets) {
    val newSetName = remember(characterSets.sets.size) { mutableStateOf("") }
    Row(Modifier.fillMaxWidth().padding(bottom = 2.dp).padding(end = 5.dp)) {
        Column(Modifier.fillMaxWidth().weight(1f)) {
            SimpleTextField(
                newSetName.value,
                onValueChange = { newSetName.value = it },
                modifier = Modifier.padding(5.dp).fillMaxWidth(),
                placeHolderText = "New set name"
            )
        }
        val enabled = newSetName.value.isNotBlank()
        Row(Modifier.height(30.dp).align(Alignment.CenterVertically)) {
            ButtonWithTooltip(
                onClick = {
                    val setName = newSetName.value.trim()
                    CharacterSetsManager.updateSet(characterUIState.name, CharacterSet(setName))
                },
                title = "Add Set",
                imageVector = Icons.Default.Add,
                shape = RoundedCornerShape(percent = 10),
                enabled = enabled,
                iconColor = if (enabled) Color.White else Color.Black,
            )
        }
    }
}

@Composable
private fun AnimatedBackgroundSelectedColor(selected: Boolean) {
    AnimatedVisibility(
        visible = selected, enter = expandHorizontally(expandFrom = Alignment.Start), exit = fadeOut()
    ) {
        Row(Modifier.fillMaxSize().background(AppColors.primaryLightColor)) {}
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CardMainContent(characterName: String, setName: String, selected: Boolean) {
    Row(Modifier.onClick { CharacterSetsManager.setSelectedSet(characterName, setName) }.fillMaxWidth().padding(5.dp)) {
        val textColor = getTextColor(selected)
        SubTitleText(
            setName,
            Modifier.align(Alignment.CenterVertically),
            maxLines = 1,
            enabledColor = textColor
        )
    }
}

@Composable
private fun getTextColor(selected: Boolean): Color {
    val color = if (selected) Color.Black else Color.White
    return animateColorAsState(color).value
}

@Composable
private fun HoverButtons(characterName: String, setName: String, isHovered: Boolean, isDeletable: Boolean) {
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.align(Alignment.TopEnd).fillMaxHeight(0.6f)) {
            if (isHovered && isDeletable) {
                DeleteButton(characterName, setName)
            }
        }
    }
}

@Composable
private fun DeleteButton(characterName: String, setName: String) {
    ButtonWithTooltip(
        { CharacterSetsManager.deleteSet(characterName, setName) },
        "Delete",
        Icons.Default.Close,
        CustomShapes.buildTrapezoidShape(bottomLeftDeltaRatio = 0.15f),
        AppColors.RED
    )
}