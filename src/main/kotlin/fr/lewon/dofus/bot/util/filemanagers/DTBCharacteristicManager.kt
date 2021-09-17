package fr.lewon.dofus.bot.util.filemanagers

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader
import java.io.File

object DTBCharacteristicManager {

    private val characteristicIdByKeyword = HashMap<String, Int>()

    init {
        val dataDirPath = System.getProperty("user.home") + "/AppData/Local/Ankama/zaap/dofus/data/"
        val d2oMapFile = File("$dataDirPath/common/Characteristics.d2o")
        val stream = ByteArrayReader(d2oMapFile.readBytes())
        require("D2O" == stream.readString(3)) { error("Invalid D2O file") }
        stream.readInt()
        while (stream.readInt() == 1) {
            val id = stream.readInt()
            val keyWord = stream.readUTF()
            characteristicIdByKeyword[keyWord] = id
            stream.readInt()
            stream.readUTF()
            stream.readInt()
            stream.readBoolean()
            stream.readInt()
            stream.readInt()
            stream.readBoolean()
        }
    }

    fun getCharacteristicId(keyWord: String): Int {
        return characteristicIdByKeyword[keyWord] ?: error("Characteristic [$keyWord] not found")
    }

}