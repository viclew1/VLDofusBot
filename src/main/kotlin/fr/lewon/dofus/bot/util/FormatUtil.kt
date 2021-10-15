package fr.lewon.dofus.bot.util

object FormatUtil {

    fun durationToStr(durationMillis: Long): String {
        val totalTimeSeconds = durationMillis / 1000
        val timeHours = totalTimeSeconds / 3600
        val timeMinutes = (totalTimeSeconds - timeHours * 60) / 60
        val timeSeconds = (totalTimeSeconds - timeHours * 3600 - timeMinutes * 60).toString().padStart(2, '0')
        if (timeHours == 0L) {
            return "${timeMinutes}M ${timeSeconds}S"
        }
        return "${timeHours}H ${timeMinutes}M ${timeSeconds}S"
    }

}