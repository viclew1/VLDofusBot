package fr.lewon.dofus.bot.gui.main.jobs

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.util.filemanagers.impl.HarvestableSetsManager

@Composable
fun HarvestableSetsList() {
    val harvestableIdsBySetName = JobsUiUtil.harvestableIdsBySetName.value
    val selectedSetName = JobsUiUtil.selectedSetName.value
    Column(Modifier.padding(5.dp).grayBoxStyle()) {
        HeaderLine()
        SetCreationLine()
        Box {
            val state = rememberScrollState()
            Column(Modifier.fillMaxHeight().padding(end = 8.dp).verticalScroll(state)) {
                for ((setName, harvestableIds) in harvestableIdsBySetName) {
                    val selected = selectedSetName == setName
                    Column(Modifier.height(30.dp)) {
                        val isHovered = remember { mutableStateOf(false) }
                        val hoverAlpha = if (isHovered.value) 1f else 0.7f
                        val backgroundColor = Color.DarkGray.copy(alpha = hoverAlpha)
                        Row(
                            modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).defaultHoverManager(isHovered)
                        ) {
                            Box(Modifier.fillMaxSize().weight(1f).background(backgroundColor)) {
                                AnimatedBackgroundSelectedColor(selected)
                                CardMainContent(setName, selected)
                                val isDeletable =
                                    !HarvestableSetsManager.defaultHarvestableIdsBySetName.contains(setName)
                                HoverButtons(setName, isHovered.value, isDeletable)
                            }
                        }
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd)
                    .background(AppColors.backgroundColor),
                adapter = rememberScrollbarAdapter(state),
            )
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun HeaderLine() {
    Row(Modifier.fillMaxWidth().height(30.dp).darkGrayBoxStyle()) {
        CommonText(
            "Harvestable Sets",
            modifier = Modifier.padding(start = 10.dp).align(Alignment.CenterVertically),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SetCreationLine() {
    val newSetName = remember { mutableStateOf("") }
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Column(Modifier.fillMaxWidth().weight(1f)) {
            SimpleTextField(
                newSetName.value,
                onValueChange = { newSetName.value = it },
                modifier = Modifier.padding(5.dp).fillMaxWidth(),
                placeHolderText = "New set name"
            )
        }
        val enabled = newSetName.value.isNotBlank()
        Row(Modifier.height(30.dp).align(Alignment.Bottom)) {
            ButtonWithTooltip(
                onClick = {
                    val setName = newSetName.value.trim()
                    HarvestableSetsManager.addSet(setName)
                    JobsUiUtil.harvestableIdsBySetName.value = HarvestableSetsManager.getHarvestableIdsBySetName()
                    JobsUiUtil.selectedSetName.value = setName
                    newSetName.value = ""
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
private fun CardMainContent(setName: String, selected: Boolean) {
    Row(Modifier.onClick { JobsUiUtil.selectedSetName.value = setName }.fillMaxWidth().padding(5.dp)) {
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
private fun HoverButtons(setName: String, isHovered: Boolean, isDeletable: Boolean) {
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.align(Alignment.TopEnd).fillMaxHeight(0.6f)) {
            if (isHovered && isDeletable) {
                DeleteButton(setName)
            }
        }
    }
}

@Composable
private fun DeleteButton(setName: String) {
    ButtonWithTooltip(
        { JobsUiUtil.deleteSet(setName) },
        "Delete",
        Icons.Default.Close,
        CustomShapes.buildTrapezoidShape(bottomLeftDeltaRatio = 0.15f),
        AppColors.RED
    )
}