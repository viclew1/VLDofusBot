package fr.lewon.dofus.bot.gui.main.scripts.selector

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.characters.CharacterActivityState
import fr.lewon.dofus.bot.gui.main.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.ScriptsUiUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilders

@Composable
fun ScriptSelectorContent() {
    val scriptBuilder = ScriptsUiUtil.getUiStateValue().currentScriptBuilder
    Column(Modifier.padding(5.dp).grayBoxStyle()) {
        Row(Modifier.fillMaxWidth().height(40.dp).darkGrayBoxStyle()) {
            CommonText(
                "Script : ",
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 10.dp),
                fontWeight = FontWeight.SemiBold
            )
            Row(Modifier.align(Alignment.CenterVertically).padding(end = 30.dp, top = 5.dp, bottom = 5.dp).weight(1f)) {
                ComboBox(
                    Modifier.fillMaxWidth().height(25.dp),
                    scriptBuilder,
                    DofusBotScriptBuilders.entries.map { it.builder },
                    { ScriptsUiUtil.updateCurrentScriptBuilder(it) },
                    { (if (it.isDev) "DEV - " else "") + it.name },
                    colors = ButtonDefaults.outlinedButtonColors(backgroundColor = AppColors.DARK_BG_COLOR)
                )
            }
            Row(Modifier.align(Alignment.CenterVertically).fillMaxHeight().padding(5.dp)) {
                PlayScriptButton()
            }
        }
        Box(Modifier.fillMaxWidth().height(100.dp).padding(5.dp)) {
            val state = rememberScrollState()
            CommonText(
                scriptBuilder.getDescription(),
                Modifier.verticalScroll(state).padding(5.dp).padding(end = 5.dp),
            )
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(state),
            )
        }
    }
}

@Composable
private fun PlayScriptButton() {
    val isStarted = ScriptsUiUtil.isScriptStarted()
    val uiState = ScriptSelectorUIUtil.uiState.value
    val selectedCharactersUIStates = CharactersUIUtil.getSelectedCharactersUIStates()
        .filter { it.activityState != CharacterActivityState.DISCONNECTED }
    val enabled = uiState.isStartButtonEnabled && selectedCharactersUIStates.isNotEmpty()
    val color = animateColorAsState(
        targetValue = if (!enabled) Color.Gray else if (isStarted) AppColors.RED else AppColors.GREEN,
        animationSpec = tween(durationMillis = 250, easing = LinearEasing)
    )
    val angle = animateFloatAsState(
        targetValue = if (isStarted) 180f else 0f,
        animationSpec = tween(durationMillis = 250, easing = LinearEasing)
    )
    Row(Modifier.fillMaxHeight()) {
        ButtonWithTooltip(
            onClick = { ScriptsUiUtil.toggleScript() },
            title = if (isStarted) "Stop" else "Start",
            shape = RoundedCornerShape(15),
            hoverBackgroundColor = Color.Gray,
            defaultBackgroundColor = AppColors.DARK_BG_COLOR,
            hoverAnimation = false,
            enabled = enabled
        ) {
            Box(Modifier.fillMaxSize().rotate(angle.value)) {
                Image(
                    if (isStarted) Icons.Default.Stop else Icons.Default.PlayArrow,
                    "",
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.tint(color.value)
                )
            }
        }
    }
}