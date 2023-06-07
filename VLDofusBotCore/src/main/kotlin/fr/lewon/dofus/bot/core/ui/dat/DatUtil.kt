package fr.lewon.dofus.bot.core.ui.dat

import com.fasterxml.jackson.databind.ObjectMapper
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.core.ui.dat.amf3.AMF3Reader
import java.io.File
import java.io.IOException

object DatUtil {

    fun <T> getDatFileContent(fileName: String, parseClass: Class<T>): T? {
        var fileNameWithExtension = fileName
        if (!fileNameWithExtension.endsWith(".dat")) {
            fileNameWithExtension += ".dat"
        }
        val file = File(VldbFilesUtil.getDofusRoamingDirectory() + "/$fileNameWithExtension")
        if (!file.exists()) {
            return null
        }
        val objectMapper = ObjectMapper()
        val result = AMF3Reader().read(readFileBytes(file))
        val json = objectMapper.writeValueAsString(result)
        return objectMapper.readValue(json, parseClass)
    }

    private fun readFileBytes(file: File): ByteArray {
        var tryCount = 0
        while (tryCount < 3) {
            try {
                return file.readBytes()
            } catch (e: IOException) {
                tryCount++
                Thread.sleep(500)
            }
        }
        error("Couldn't read file content (${file.absolutePath})")
    }
}