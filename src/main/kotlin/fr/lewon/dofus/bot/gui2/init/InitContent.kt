package fr.lewon.dofus.bot.gui2.init

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.gui2.util.AppColors

@Composable
fun InitContent() {
    Column(
        modifier = Modifier.background(AppColors.VERY_DARK_BG_COLOR).fillMaxSize().padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        for (initTask in InitUIUtil.INIT_TASKS_UI_STATES) {
            initTaskLine(initTask.value)
            Divider(Modifier.fillMaxWidth(0.95f).align(Alignment.CenterHorizontally))
        }
        Spacer(Modifier.fillMaxHeight().weight(1f))
    }
}

@Composable
private fun initTaskLine(initTaskUIState: InitTaskUIState) {
    Row(modifier = Modifier.fillMaxWidth()) {
        val color = when {
            initTaskUIState.success -> Color.Green
            initTaskUIState.executed -> Color.Red
            initTaskUIState.executing -> AppColors.primaryLightColor
            else -> Color.LightGray
        }
        Text(
            text = initTaskUIState.label,
            modifier = Modifier.width(200.dp),
            color = color,
            fontSize = 13.sp
        )

        if (initTaskUIState.executing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(0.9f).align(Alignment.CenterVertically),
                color = color
            )
        } else if (initTaskUIState.executed) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(0.9f).align(Alignment.CenterVertically),
                color = color,
                progress = 1.0f,
            )
        }
    }
}