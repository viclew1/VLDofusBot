package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.selector

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
import fr.lewon.dofus.bot.gui2.custom.ButtonWithTooltip
import fr.lewon.dofus.bot.gui2.custom.ComboBox
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIUtil
import fr.lewon.dofus.bot.gui2.util.AppColors

@Composable
fun ScriptSelectorContent() {
    val scriptBuilder = ScriptTabsUIUtil.getCurrentScriptBuilder()
    Column {
        Spacer(Modifier.height(5.dp))
        Row(Modifier.height(40.dp)) {
            CommonText(
                "Script : ",
                modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
                fontWeight = FontWeight.SemiBold
            )
            Row(Modifier.align(Alignment.CenterVertically).padding(end = 30.dp, top = 5.dp, bottom = 5.dp).weight(1f)) {
                ComboBox(
                    Modifier.fillMaxWidth().height(30.dp),
                    scriptBuilder,
                    ScriptTabsUIUtil.scripts,
                    { ScriptTabsUIUtil.updateCurrentScriptBuilder(it) },
                    { (if (it.isDev) "DEV - " else "") + it.name },
                    colors = ButtonDefaults.outlinedButtonColors(backgroundColor = AppColors.DARK_BG_COLOR),
                    borderColor = Color.Gray
                )
            }
            Row(Modifier.align(Alignment.CenterVertically).fillMaxHeight().padding(5.dp)) {
                PlayScriptButton()
            }
        }
        Box(Modifier.fillMaxWidth().height(100.dp).padding(5.dp).grayBoxStyle()) {
            val state = rememberScrollState()
            CommonText(
                scriptBuilder.getDescription(),
                Modifier.verticalScroll(state).padding(10.dp),
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
    val isStarted = ScriptTabsUIUtil.isScriptStarted()
    val uiState = ScriptSelectorUIUtil.uiState.value
    val selectedCharactersUIStates = CharactersUIUtil.getSelectedCharactersUIStates()
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
            onClick = { ScriptTabsUIUtil.toggleScript() },
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