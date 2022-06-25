package fr.lewon.dofus.bot.gui2.main.scripts.status

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import org.apache.commons.lang3.StringUtils
import java.time.LocalDateTime
import java.util.concurrent.ArrayBlockingQueue

object StatusBarUIState {

    private val oldMessages = ArrayBlockingQueue<String>(10)
    val currentStatusState = mutableStateOf("")
    val historyState = mutableStateOf<List<String>>(emptyList())

    @Synchronized
    fun changeText(character: DofusCharacter, text: String) {
        if (currentStatusState.value.isNotEmpty()) {
            if (!oldMessages.offer(currentStatusState.value)) {
                oldMessages.poll()
                oldMessages.offer(currentStatusState.value)
            }
        }
        historyState.value = oldMessages.toList()
        val ldt = LocalDateTime.now()
        val hours = StringUtils.leftPad(ldt.hour.toString(), 2, "0")
        val minutes = StringUtils.leftPad(ldt.minute.toString(), 2, "0")
        val seconds = StringUtils.leftPad(ldt.second.toString(), 2, "0")
        val timeStamp = "$hours:$minutes:$seconds"
        currentStatusState.value = "$timeStamp : ${character.pseudo} - $text"
    }

}