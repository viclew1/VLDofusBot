package fr.lewon.dofus.bot.gui.main.exploration

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.ButtonWithTooltip
import fr.lewon.dofus.bot.gui.main.exploration.map.ExplorationMapContent
import fr.lewon.dofus.bot.gui.util.AppColors

@Composable
fun ExplorationHelperContent() {
    Box(Modifier.fillMaxSize()) {
        ExplorationMapContent()
        Row(Modifier.align(Alignment.TopEnd)) {
            ConnectedCharactersContent()
            SelectedSubAreaContent()
        }
        Column(Modifier.padding(5.dp)) {
            val currentWorldMapHelper = ExplorationUIUtil.worldMapHelper.value
            for (worldMapHelper in ExplorationUIUtil.worldMapHelpers) {
                val selected = worldMapHelper == currentWorldMapHelper
                Row(Modifier.height(40.dp)) {
                    ButtonWithTooltip(
                        onClick = { ExplorationUIUtil.worldMapHelper.value = worldMapHelper },
                        title = worldMapHelper.name,
                        imagePainter = worldMapHelper.icon.imagePainter,
                        shape = RoundedCornerShape(percent = 10),
                        hoverBackgroundColor = Color.Gray,
                        defaultBackgroundColor = AppColors.DARK_BG_COLOR,
                        width = 40.dp,
                        delayMillis = 0,
                        imageModifier = if (selected) {
                            Modifier.border(BorderStroke(2.dp, AppColors.primaryLightColor))
                        } else Modifier
                    )
                }
                Spacer(Modifier.height(5.dp))
            }
        }
    }
}