package fr.lewon.dofus.bot.util.filemanagers

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader
import java.io.File

object DTBLabelManager {

    private val d2iStream: ByteArrayReader
    private val hintNameIdById = HashMap<Int, Int>()
    private val subAreaNameIdById = HashMap<Int, Int>()
    private val areaIdBySubAreaId = HashMap<Int, Int>()
    private val areaNameIdById = HashMap<Int, Int>()
    private val indexes = HashMap<Int, Int>()
    private val textIndexes = HashMap<String, Int>()

    init {
        val dataDirPath = System.getProperty("user.home") + "/AppData/Local/Ankama/zaap/dofus/data/"
        val d2iFile = File("$dataDirPath/i18n/i18n_fr.d2i")
        d2iStream = ByteArrayReader(d2iFile.readBytes())
        initHintNameIdById(File("$dataDirPath/common/PointOfInterest.d2o"))
        initSubAreaNameById(File("$dataDirPath/common/SubAreas.d2o"))
        initAreaNameIdById(File("$dataDirPath/common/Areas.d2o"))
        initIndexes()
    }

    private fun initHintNameIdById(file: File) {
        val stream = ByteArrayReader(file.readBytes())
        require("D2O" == stream.readString(3)) { error("Invalid D2O file") }
        val tableSize = stream.readInt()
        for (i in 0 until tableSize / 16) {
            stream.readInt()
            val id = stream.readInt()
            val nameId = stream.readInt()
            stream.readInt()
            hintNameIdById[id] = nameId
        }
    }

    private fun initSubAreaNameById(file: File) {
        val stream = ByteArrayReader(file.readBytes())
        require("D2O" == stream.readString(3)) { error("Invalid D2O file") }
        stream.readInt()
        while (stream.readInt() == 2) {
            val id = stream.readInt()
            val nameId = stream.readInt()
            val areaId = stream.readInt()
            for (i in 0 until stream.readInt()) {
                for (j in 0 until stream.readInt()) {
                    stream.readInt()
                }
            }
            for (i in 0 until stream.readInt()) {
                stream.readDouble()
            }
            stream.skip(20)
            for (i in 0 until stream.readInt()) {
                stream.readInt()
            }
            stream.readInt()
            for (i in 0 until stream.readInt()) {
                stream.readInt()
            }
            stream.skip(13)
            for (i in 0 until stream.readInt()) {
                stream.readInt()
            }
            for (i in 0 until stream.readInt()) {
                stream.readDouble()
            }
            for (i in 0 until stream.readInt()) {
                stream.readDouble()
            }
            stream.readBoolean()
            for (i in 0 until stream.readInt()) {
                stream.readInt()
            }

            var result = 0
            while (result != -1) {
                result = stream.readInt()
            }
            for (i in 0 until stream.readInt()) {
                stream.readInt()
            }
            stream.readInt()
            subAreaNameIdById[id] = nameId
            areaIdBySubAreaId[id] = areaId
        }
    }

    private fun initAreaNameIdById(file: File) {
        val stream = ByteArrayReader(file.readBytes())
        require("D2O" == stream.readString(3)) { error("Invalid D2O file") }
        stream.readInt()
        while (stream.readInt() == 2) {
            val id = stream.readInt()
            val nameId = stream.readInt()
            stream.skip(32)
            areaNameIdById[id] = nameId
        }
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

    fun getSubAreaLabel(subAreaId: Int): String {
        val key = subAreaNameIdById[subAreaId] ?: error("No nameId found for subAreaId [$subAreaId]")
        return getLabel(key)
    }

    fun getAreaLabel(subAreaId: Int): String {
        val areaId = areaIdBySubAreaId[subAreaId] ?: error("No areaId found for subAreaId [$subAreaId]")
        val key = areaNameIdById[areaId] ?: error("No nameId found for areaId [$areaId]")
        return getLabel(key)
    }

    fun getLabel(key: Int): String {
        val pointer = indexes[key] ?: error("Couldn't find resource with id : $key")
        d2iStream.setPosition(pointer)
        return d2iStream.readUTF()
    }

    fun getLabel(textKey: String): String {
        val pointer = textIndexes[textKey] ?: error("Couldn't find resource with id : $textKey")
        d2iStream.setPosition(pointer)
        return d2iStream.readUTF()
    }

    fun getHintLabel(hintLabelId: Int): String {
        val key = hintNameIdById[hintLabelId] ?: return "???"
        return getLabel(key)
    }

}