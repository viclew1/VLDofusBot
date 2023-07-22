package fr.lewon.dofus.bot.util

import org.apache.commons.lang3.StringUtils
import java.time.LocalDateTime

object FormatUtil {

    fun durationToStr(durationMillis: Long): String {
        val totalTimeSeconds = durationMillis / 1000
        val timeHours = totalTimeSeconds / 3600
        val timeMinutes = (totalTimeSeconds - timeHours * 3600) / 60
        val timeSeconds = (totalTimeSeconds - timeHours * 3600 - timeMinutes * 60).toString().padStart(2, '0')
        if (timeHours == 0L) {
            return "${timeMinutes}M ${timeSeconds}S"
        }
        return "${timeHours}H ${timeMinutes}M ${timeSeconds}S"
    }

    fun localDateTimeToStr(localDateTime: LocalDateTime): String {
        val hours = StringUtils.leftPad(localDateTime.hour.toString(), 2, "0")
        val minutes = StringUtils.leftPad(localDateTime.minute.toString(), 2, "0")
        val seconds = StringUtils.leftPad(localDateTime.second.toString(), 2, "0")
        return "$hours:$minutes:$seconds"
    }

}