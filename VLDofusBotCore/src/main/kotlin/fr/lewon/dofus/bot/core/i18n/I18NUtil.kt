package fr.lewon.dofus.bot.core.i18n

import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import java.io.File

object I18NUtil {

    private lateinit var d2iStream: ByteArrayReader
    private lateinit var indexes: HashMap<Int, Int>
    private lateinit var textIndexes: HashMap<String, Int>

    fun init() {
        indexes = HashMap()
        textIndexes = HashMap()
        val dataDirPath = "${VldbFilesUtil.getDofusDirectory()}/data/"
        val d2iFile = File("$dataDirPath/i18n/i18n_fr.d2i")
        d2iStream = ByteArrayReader(d2iFile.readBytes())
        initIndexes()
    }

    private fun initIndexes() {
        val unDiacriticalIndex = HashMap<Int, Int>()
        var keyCount = 0
        var textCount = 0
        d2iStream.setPosition(d2iStream.readInt())
        var indexesLength = d2iStream.readInt()
        var i = 0
        while (i < indexesLength) {
            val key = d2iStream.readInt()
            val diacriticalText = d2iStream.readBoolean()
            val pointer = d2iStream.readInt()
            indexes[key] = pointer
            keyCount++
            if (diacriticalText) {
                keyCount++
                i += 4
                unDiacriticalIndex[key] = d2iStream.readInt()
            } else {
                unDiacriticalIndex[key] = pointer
            }
            i += 9
        }

        indexesLength = d2iStream.readInt()
        while (indexesLength > 0) {
            val strLen = d2iStream.readUnsignedShort()
            val textKey = d2iStream.readString(strLen)
            val pointer = d2iStream.readInt()
            val sz = 2 + strLen + 4
            textCount++
            textIndexes[textKey] = pointer
            indexesLength -= sz
        }
    }

    fun hasLabel(key: Int): Boolean {
        return indexes[key] != null
    }

    fun getLabel(key: Int): String? {
        val pointer = indexes[key] ?: return null
        d2iStream.setPosition(pointer)
        return d2iStream.readUTF()
    }

    fun getLabel(textKey: String): String {
        val pointer = textIndexes[textKey] ?: error("Couldn't find resource with id : $textKey")
        d2iStream.setPosition(pointer)
        return d2iStream.readUTF()
    }

}