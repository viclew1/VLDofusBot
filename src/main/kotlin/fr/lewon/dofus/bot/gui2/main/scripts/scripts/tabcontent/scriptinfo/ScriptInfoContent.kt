package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.scriptinfo

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.util.AppColors
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.FormatUtil
import fr.lewon.dofus.bot.util.script.ScriptRunner
import kotlinx.coroutines.delay

@Composable
fun ScriptInfoContent(character: DofusCharacter) {
    val runningScript = ScriptRunner.getRunningScript(character)
    val text = remember { mutableStateOf("") }
    val color = mutableStateOf(AppColors.RED)
    if (runningScript != null) {
        val timeSinceExec = remember { mutableStateOf(System.currentTimeMillis() - runningScript.startTime) }
        LaunchedEffect(Unit) {
            while (true) {
                delay(1000)
                timeSinceExec.value = System.currentTimeMillis() - runningScript.startTime
            }
        }
        val durationStr = FormatUtil.durationToStr(timeSinceExec.value)
        text.value = "${character.pseudo} : Running time : $durationStr - ${runningScript.scriptBuilder.name}"
        color.value = Color.White
    } else {
        text.value = "${character.pseudo} : Ready"
        color.value = AppColors.GREEN
    }
    CommonText(
        text.value,
        modifier = Modifier.padding(4.dp),
        fontWeight = FontWeight.SemiBold,
        enabledColor = color.value
    )
}