package fr.lewon.dofus.bot.gui2.main.scripts.characters

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.*
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTab
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIState
import fr.lewon.dofus.bot.gui2.util.AppColors
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager

@Composable
fun CharacterCardContent(character: DofusCharacter, selected: Boolean) {
    val isHovered = remember { mutableStateOf(false) }
    val hoverAlpha = if (isHovered.value) 1f else 0.7f
    val backgroundColor = Color.DarkGray.copy(alpha = hoverAlpha)
    Row(modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).defaultHoverManager(isHovered)) {
        Box(Modifier.fillMaxSize().weight(1f).background(backgroundColor)) {
            AnimatedBackgroundSelectedColor(selected)
            CardMainContent(character, selected)
            HoverButtons(character, isHovered)
        }
        GlobalScriptSelectedCheckbox(character)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CardMainContent(character: DofusCharacter, selected: Boolean) {
    val breedAssets = BreedAssetManager.getAssets(character.dofusClassId)
    val characterState = CharactersUIState.getCharacterActivityState(character).value
    Row(Modifier.onPointerEvent(PointerEventType.Press, PointerEventPass.Main) { event ->
        if (event.buttons.isPrimaryPressed) {
            CharactersUIState.selectCharacter(character)
        }
    }.fillMaxWidth()) {
        Image(painter = breedAssets.simpleIconPainter, "", contentScale = ContentScale.FillHeight)
        Spacer(Modifier.width(10.dp))
        Row(Modifier.size(18.dp, 18.dp).align(Alignment.CenterVertically)) {
            DefaultTooltipArea(characterState.labelBuilder(character), 20.dp) {
                Image(characterState.uiResource.imagePainter, "", Modifier.fillMaxSize())
            }
        }
        Spacer(Modifier.width(10.dp))
        val textColor = getTextColor(selected)
        SubTitleText(
            character.pseudo,
            Modifier.align(Alignment.CenterVertically),
            maxLines = 1,
            enabledColor = textColor
        )
    }
}

@Composable
fun getTextColor(selected: Boolean): Color {
    val color = if (selected) Color.Black else Color.White
    return if (CharactersUIState.isDragging.value) {
        color
    } else {
        val animatedColor by animateColorAsState(color)
        animatedColor
    }
}

@Composable
private fun AnimatedBackgroundSelectedColor(selected: Boolean) {
    if (CharactersUIState.isDragging.value && selected) {
        Row(Modifier.fillMaxSize().background(AppColors.primaryLightColor)) {}
    } else if (!CharactersUIState.isDragging.value) {
        AnimatedVisibility(
            visible = selected, enter = expandHorizontally(expandFrom = Alignment.Start), exit = fadeOut()
        ) {
            Row(Modifier.fillMaxSize().background(AppColors.primaryLightColor)) {}
        }
    }
}

@Composable
private fun HoverButtons(character: DofusCharacter, isHovered: MutableState<Boolean>) {
    if (!CharactersUIState.isDragging.value) {
        Box(Modifier.fillMaxSize()) {
            if (isHovered.value) {
                Box(Modifier.align(Alignment.TopEnd).fillMaxHeight(0.6f)) {
                    Row {
                        EditButton(character)
                        Spacer(Modifier.width(2.dp))
                        DeleteButton(character)
                    }
                }
            }
        }
    }
}

@Composable
private fun GlobalScriptSelectedCheckbox(character: DofusCharacter) {
    AnimatedVisibility(
        visible = ScriptTabsUIState.currentPage.value == ScriptTab.GLOBAL,
        enter = expandHorizontally(expandFrom = Alignment.End),
        exit = shrinkHorizontally(shrinkTowards = Alignment.End)
    ) {
        val characterState = CharactersUIState.getCharacterActivityState(character).value
        val enabled = characterState != CharacterActivityState.DISCONNECTED
        val checked = CharactersUIState.getCharacterCheckedState(character)
        val defaultBackgroundColor = if (enabled) Color.Gray else Color.DarkGray
        val checkBoxBackgroundColor = defaultBackgroundColor.copy(alpha = 0.5f)
        val modifier = if (enabled) Modifier.handPointerIcon() else Modifier
        Row(modifier.background(checkBoxBackgroundColor).width(30.dp)) {
            Checkbox(
                checked.value,
                { checked.value = !checked.value },
                Modifier.fillMaxWidth(),
                colors = CheckboxDefaults.colors(checkmarkColor = Color.Black, disabledColor = Color.Black),
                enabled = CharactersUIState.getCharacterActivityState(character).value != CharacterActivityState.DISCONNECTED
            )
        }
    }
}

@Composable
private fun EditButton(character: DofusCharacter) {
    ButtonWithTooltip(
        { println("Edit ${character.pseudo}") },
        "Edit",
        Icons.Default.Edit,
        CustomShapes.buildTrapezoidShape(bottomLeftDeltaRatio = 0.25f),
        AppColors.ORANGE,
        0.25f / 2
    )
}

@Composable
private fun DeleteButton(character: DofusCharacter) {
    ButtonWithTooltip(
        { println("Delete ${character.pseudo}") },
        "Delete",
        Icons.Default.Close,
        CustomShapes.buildTrapezoidShape(),
        AppColors.RED
    )
}

@Composable
private fun ButtonWithTooltip(
    onClick: () -> Unit,
    title: String,
    imageVector: ImageVector,
    shape: Shape,
    hoverBackgroundColor: Color = AppColors.backgroundColor,
    iconDeltaWidth: Float = 0f
) {
    val isHovered = remember { mutableStateOf(false) }
    Row(Modifier.width(30.dp).defaultHoverManager(isHovered)) {
        DefaultTooltipArea(title, 20.dp, 1000) {
            Button(
                onClick,
                modifier = Modifier.handPointerIcon().padding(0.dp),
                shape = shape,
                colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.backgroundColor),
                contentPadding = PaddingValues(0.dp)
            ) {
                val modifier = if (isHovered.value) Modifier.background(hoverBackgroundColor) else Modifier
                Box(modifier.fillMaxSize()) {
                    if (iconDeltaWidth > 0) {
                        Spacer(Modifier.fillMaxSize(iconDeltaWidth))
                    }
                    Image(
                        imageVector,
                        "",
                        Modifier.fillMaxSize(0.95f).align(Alignment.Center),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
            }
        }
    }
}