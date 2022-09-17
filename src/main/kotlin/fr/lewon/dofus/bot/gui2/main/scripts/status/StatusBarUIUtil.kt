package fr.lewon.dofus.bot.gui2.main.scripts.status

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import org.apache.commons.lang3.StringUtils
import java.time.LocalDateTime

object StatusBarUIUtil {

    private const val MAX_HISTORY_SIZE = 10
    val UI_STATE = mutableStateOf(StatusBarUIState())

    @Synchronized
    fun changeText(character: DofusCharacter, text: String) {
        val statusBarUIState = UI_STATE.value
        val newOldMessages = ArrayList(statusBarUIState.oldMessages)
        if (statusBarUIState.currentStatus.isNotEmpty()) {
            if (statusBarUIState.oldMessages.size >= MAX_HISTORY_SIZE) {
                newOldMessages.removeAt(0)
            }
            newOldMessages.add(statusBarUIState.currentStatus)
        }
        val ldt = LocalDateTime.now()
        val hours = StringUtils.leftPad(ldt.hour.toString(), 2, "0")
        val minutes = StringUtils.leftPad(ldt.minute.toString(), 2, "0")
        val seconds = StringUtils.leftPad(ldt.second.toString(), 2, "0")
        val timeStamp = "$hours:$minutes:$seconds"
        val newStatus = "$timeStamp : ${character.pseudo} - $text"
        UI_STATE.value = statusBarUIState.copy(oldMessages = newOldMessages, currentStatus = newStatus)
    }

}