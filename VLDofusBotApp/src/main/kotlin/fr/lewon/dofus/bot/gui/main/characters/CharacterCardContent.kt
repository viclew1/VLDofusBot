package fr.lewon.dofus.bot.gui.main.characters

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager

@Composable
fun CharacterCardContent(characterUIState: CharacterUIState, selected: Boolean, canSelectMultipleCharacters: Boolean) {
    val isHovered = remember { mutableStateOf(false) }
    val hoverAlpha = if (isHovered.value) 1f else 0.7f
    val backgroundColor = Color.DarkGray.copy(alpha = hoverAlpha)
    Row(modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).defaultHoverManager(isHovered)) {
        CharacterStateIndicator(characterUIState)
        Box(Modifier.fillMaxSize().weight(1f).background(backgroundColor)) {
            AnimatedBackgroundSelectedColor(selected)
            CardMainContent(characterUIState, selected, canSelectMultipleCharacters)
            HoverButtons(characterUIState, isHovered)
        }
        AnimatedVisibility(
            visible = canSelectMultipleCharacters,
            enter = expandHorizontally(expandFrom = Alignment.End),
            exit = shrinkHorizontally(shrinkTowards = Alignment.End)
        ) {
            GlobalScriptSelectedCheckbox(characterUIState)
        }
    }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CardMainContent(
    characterUIState: CharacterUIState,
    selected: Boolean,
    canSelectMultipleCharacters: Boolean,
) {
    val breedAssets = BreedAssetManager.getAssets(characterUIState.dofusClassId)
    Row(Modifier.onClick {
        CharactersUIUtil.selectCharacter(characterUIState.name)
    }.onClick(keyboardModifiers = { canSelectMultipleCharacters && isCtrlPressed }) {
        CharactersUIUtil.toggleSelect(characterUIState.name)
    }.fillMaxWidth()) {
        Image(painter = breedAssets.simpleIconPainter, "", contentScale = ContentScale.FillHeight)
        Spacer(Modifier.width(10.dp))
        val textColor = getTextColor(selected)
        SubTitleText(
            characterUIState.name,
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
private fun AnimatedBackgroundSelectedColor(selected: Boolean) {
    AnimatedVisibility(
        visible = selected, enter = expandHorizontally(expandFrom = Alignment.Start), exit = fadeOut()
    ) {
        Row(Modifier.fillMaxSize().background(AppColors.primaryLightColor)) {}
    }
}

@Composable
private fun HoverButtons(characterUIState: CharacterUIState, isHovered: MutableState<Boolean>) {
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.align(Alignment.TopEnd).fillMaxHeight(0.6f)) {
            if (isHovered.value && characterUIState.activityState == CharacterActivityState.DISCONNECTED) {
                DeleteButton(characterUIState)
            }
        }
    }
}

@Composable
private fun DeleteButton(characterUIState: CharacterUIState) {
    ButtonWithTooltip(
        { CharacterManager.removeCharacter(characterUIState.name) },
        "Delete",
        Icons.Default.Close,
        CustomShapes.buildTrapezoidShape(bottomLeftDeltaRatio = 0.15f),
        AppColors.RED
    )
}

@Composable
private fun GlobalScriptSelectedCheckbox(characterUIState: CharacterUIState) {
    val selectedCharacterNames = CharactersUIUtil.getSelectedCharactersNames()
    val characterActivityState = characterUIState.activityState
    val enabled = characterActivityState != CharacterActivityState.DISCONNECTED
    val defaultBackgroundColor = if (enabled) Color.Gray else Color.DarkGray
    val checkBoxBackgroundColor = defaultBackgroundColor.copy(alpha = 0.5f)
    val modifier = if (enabled) Modifier.handPointerIcon() else Modifier
    Row(modifier.background(checkBoxBackgroundColor).width(30.dp)) {
        Checkbox(
            characterUIState.name in selectedCharacterNames,
            { CharactersUIUtil.toggleSelect(characterUIState.name) },
            Modifier.fillMaxWidth(),
            colors = CheckboxDefaults.colors(checkmarkColor = Color.Black, disabledColor = Color.Black),
            enabled = enabled
        )
    }
}