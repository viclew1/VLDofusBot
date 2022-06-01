package fr.lewon.dofus.bot.util

import java.text.Normalizer

object StringUtil {

    fun removeAccents(str: String): String {
        val temp = Normalizer.normalize(str.lowercase(), Normalizer.Form.NFD)
        val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        return regex.replace(temp, "")
    }

}