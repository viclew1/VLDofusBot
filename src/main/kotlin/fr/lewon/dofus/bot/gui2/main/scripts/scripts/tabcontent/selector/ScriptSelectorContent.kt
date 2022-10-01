package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.selector

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.ComboBox
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.custom.DefaultTooltipArea
import fr.lewon.dofus.bot.gui2.custom.handPointerIcon
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.parameters.ScriptParametersUIUtil
import fr.lewon.dofus.bot.gui2.util.AppColors
import fr.lewon.dofus.bot.util.script.ScriptRunner
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun ScriptSelectorContent() {
    val scriptBuilder = ScriptTabsUIUtil.getCurrentScriptBuilder()
    Column {
        Row(Modifier.height(40.dp)) {
            CommonText(
                "Script : ",
                modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
                fontWeight = FontWeight.SemiBold
            )
            Row(Modifier.padding(end = 30.dp, top = 5.dp, bottom = 5.dp).weight(1f)) {
                ComboBox(
                    Modifier.fillMaxWidth().height(30.dp),
                    scriptBuilder,
                    ScriptTabsUIUtil.scripts,
                    { ScriptTabsUIUtil.updateCurrentScriptBuilder(it) },
                    { (if (it.isDev) "DEV - " else "") + it.name })
            }
            Row(Modifier.align(Alignment.CenterVertically).width(40.dp).fillMaxHeight().padding(10.dp)) {
                PlayScriptButton()
            }
        }
        Box(
            Modifier.fillMaxWidth().height(100.dp).padding(5.dp).border(BorderStroke(1.dp, Color.Gray))
                .background(AppColors.DARK_BG_COLOR)
        ) {
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
    val dyRatio = if (isStarted) 0f else 0.5f
    val uiState = ScriptSelectorUIUtil.uiState.value
    val enabled = uiState.isStartButtonEnabled
    val color = animateColorAsState(if (!enabled) Color.Gray else if (isStarted) AppColors.RED else AppColors.GREEN)
    val animatedDyRatio = animateFloatAsState(targetValue = dyRatio)

    val text = if (isStarted) "Stop" else "Start"
    DefaultTooltipArea(text, tooltipHeight = 20.dp, delayMillis = 1000) {

        val shape = GenericShape { size, _ ->
            moveTo(0f, 0f)
            lineTo(size.width, animatedDyRatio.value * size.height)
            lineTo(size.width, size.height - animatedDyRatio.value * size.height)
            lineTo(0f, size.height)
        }
        Surface(
            Modifier.fillMaxSize(),
            shape = shape,
            elevation = 5.dp,
            color = color.value
        ) {
            var modifier = Modifier.fillMaxSize()
            if (enabled) {
                modifier = modifier.handPointerIcon().clickable {
                    GlobalScope.launch {
                        ScriptSelectorUIUtil.uiState.value = uiState.copy(isStartButtonEnabled = false)
                        val selectedCharacters = CharactersUIUtil.getSelectedCharacters()
                        if (isStarted) {
                            selectedCharacters.forEach { ScriptRunner.stopScript(it) }
                        } else {
                            val scriptBuilder = ScriptTabsUIUtil.getCurrentScriptBuilder()
                            val scriptValues = ScriptParametersUIUtil.getScriptValuesStore().getValues(scriptBuilder)
                            selectedCharacters.forEach { ScriptRunner.runScript(it, scriptBuilder, scriptValues) }
                        }
                    }
                }
            }
            Box(modifier)
        }
    }
}