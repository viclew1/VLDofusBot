package fr.lewon.dofus.bot.gui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.defaultHoverManager
import fr.lewon.dofus.bot.gui.custom.handPointerIcon
import fr.lewon.dofus.bot.gui.main.status.StatusBarContent
import fr.lewon.dofus.bot.gui.util.AppColors

@Composable
fun MainContent() {
    val modifier = Modifier.fillMaxSize()
        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {}
    PressDraggable(modifier) {
        TooltipManageable {
            Row(Modifier.fillMaxSize()) {
                MainNavigationRail()
                Box(Modifier.fillMaxSize()) {
                    Row(Modifier.padding(bottom = 30.dp)) {
                        MainContentUIUtil.mainContentUIState.value.currentAppContent.content()
                    }
                    Row(Modifier.align(Alignment.BottomCenter)) {
                        StatusBarContent()
                    }
                }
            }
        }
    }
}

@Composable
private fun MainNavigationRail() {
    NavigationRail(modifier = Modifier.fillMaxHeight().width(64.dp)) {
        for (appContent in MainAppContent.values()) {
            Divider(Modifier.fillMaxWidth(0.8f).height(2.dp).align(Alignment.CenterHorizontally))
            val uiState = MainContentUIUtil.mainContentUIState.value
            val selected = uiState.currentAppContent == appContent
            val isHovered = remember { mutableStateOf(false) }
            NavigationRailItem(selected,
                { MainContentUIUtil.mainContentUIState.value = uiState.copy(currentAppContent = appContent) },
                modifier = Modifier.defaultHoverManager(isHovered).handPointerIcon().fillMaxWidth()
                    .padding(vertical = 3.dp),
                icon = {
                    val iconPainter = appContent.uiResource.imagePainter
                    val backgroundColor = Color.Transparent
                    TooltipTarget(appContent.title, modifier = Modifier.fillMaxSize()) {
                        Row(modifier = Modifier.height(64.dp).fillMaxWidth()) {
                            SelectedIndicator(Modifier.align(Alignment.CenterVertically), selected, isHovered.value)
                            Image(iconPainter, "", Modifier.fillMaxSize().background(backgroundColor))
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun SelectedIndicator(modifier: Modifier, selected: Boolean, hovered: Boolean) {
    Box(modifier.fillMaxHeight().width(4.dp)) {
        AnimatedIndicator(Modifier.align(Alignment.Center), selected, 0.7f) { it / 2 }
        AnimatedIndicator(Modifier.align(Alignment.Center), hovered, 0.4f)
    }
}

@Composable
private fun AnimatedIndicator(
    modifier: Modifier,
    visible: Boolean,
    maxHeightRatio: Float,
    initialHeight: (fullHeight: Int) -> Int = { 0 }
) {
    val shape = CutCornerShape(topEndPercent = 100, bottomEndPercent = 100)
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = expandVertically(expandFrom = Alignment.CenterVertically, initialHeight = initialHeight),
        exit = shrinkVertically(shrinkTowards = Alignment.CenterVertically)
    ) {
        Box(Modifier.fillMaxWidth().fillMaxHeight(maxHeightRatio).clip(shape).background(AppColors.primaryColor))
    }
}
