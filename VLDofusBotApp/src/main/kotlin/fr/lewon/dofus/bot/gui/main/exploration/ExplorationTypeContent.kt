package fr.lewon.dofus.bot.gui.main.exploration

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.ButtonWithTooltip
import fr.lewon.dofus.bot.gui.main.exploration.path.ExplorePathContent
import fr.lewon.dofus.bot.gui.main.exploration.subarea.ExploreSubAreasContent
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.scripts.impl.ExploreMapsScriptBuilder

@Composable
fun ExplorationTypeContent() {
    Column {
        ExplorationTypeSelectorContent()
        when (ExplorationUIUtil.explorationTypeUiState.value) {
            ExploreMapsScriptBuilder.ExplorationType.SubArea -> ExploreSubAreasContent()
            ExploreMapsScriptBuilder.ExplorationType.Path -> ExplorePathContent()
        }
    }
}

@Composable
private fun ExplorationTypeSelectorContent() {
    Row {
        val buttonSize = 35.dp
        for (explorationType in ExploreMapsScriptBuilder.ExplorationType.entries) {
            val selected = ExplorationUIUtil.explorationTypeUiState.value == explorationType
            Row(Modifier.padding(3.dp)) {
                Row(Modifier.height(buttonSize)) {
                    ButtonWithTooltip(
                        onClick = { ExplorationUIUtil.explorationTypeUiState.value = explorationType },
                        "${explorationType.strValue} mode",
                        imagePainter = explorationType.icon.imagePainter,
                        shape = RoundedCornerShape(percent = 10),
                        hoverBackgroundColor = Color.Gray,
                        defaultBackgroundColor = AppColors.DARK_BG_COLOR,
                        width = buttonSize,
                        delayMillis = 0,
                        imageModifier = if (selected) {
                            Modifier.border(BorderStroke(2.dp, AppColors.primaryLightColor))
                        } else Modifier
                    )
                }
            }
        }
    }
}
