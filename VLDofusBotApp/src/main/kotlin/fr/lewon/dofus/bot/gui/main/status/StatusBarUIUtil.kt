package fr.lewon.dofus.bot.gui.main.status

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.FormatUtil
import java.time.LocalDateTime

object StatusBarUIUtil : ComposeUIUtil() {

    private const val MAX_HISTORY_SIZE = 10
    val statusBarUIState = mutableStateOf(StatusBarUIState())

    @Synchronized
    fun changeText(character: DofusCharacter, text: String) {
        val statusBarUIState = statusBarUIState.value
        val newOldMessages = ArrayList(statusBarUIState.oldMessages)
        if (statusBarUIState.currentStatus.isNotEmpty()) {
            if (statusBarUIState.oldMessages.size >= MAX_HISTORY_SIZE) {
                newOldMessages.removeAt(0)
            }
            newOldMessages.add(statusBarUIState.currentStatus)
        }
        val ldt = LocalDateTime.now()
        val newStatus = "${FormatUtil.localDateTimeToStr(ldt)} : ${character.name} - $text"
        StatusBarUIUtil.statusBarUIState.value =
            statusBarUIState.copy(oldMessages = newOldMessages, currentStatus = newStatus)
    }

}