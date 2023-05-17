package fr.lewon.dofus.bot.core.d2o.gamedata

import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import java.util.*

class GameDataField(val name: String) {

    companion object {
        private const val NULL_IDENTIFIER = -1431655766
    }

    private val innerReadMethods = LinkedList<(String, ByteArrayReader, Int) -> Any>()
    lateinit var readData: (String, ByteArrayReader, Int) -> Any

    fun readType(stream: ByteArrayReader) {
        readData = getReadMethod(stream.readInt(), stream)
    }

    private fun getReadMethod(type: Int, stream: ByteArrayReader): (String, ByteArrayReader, Int) -> Any {
        return when (GameDataTypeEnum.getDataType(type)) {
            GameDataTypeEnum.INT -> { _, bar, _ -> bar.readInt() }
            GameDataTypeEnum.BOOLEAN -> { _, bar, _ -> bar.readBoolean() }
            GameDataTypeEnum.STRING -> { _, bar, _ -> bar.readUTF() }
            GameDataTypeEnum.NUMBER -> { _, bar, _ -> bar.readDouble() }
            GameDataTypeEnum.I18N -> { _, bar, _ -> bar.readInt() }
            GameDataTypeEnum.UINT -> { _, bar, _ -> bar.readInt() }
            GameDataTypeEnum.VECTOR -> {
                stream.readUTF()
                innerReadMethods.addFirst(getReadMethod(stream.readInt(), stream))
                this::readVector
            }
            else -> { moduleName, bar, _ -> readObject(moduleName, bar) }
        }
    }

    private fun readVector(moduleName: String, stream: ByteArrayReader, innerIndex: Int): Any {
        val len = stream.readInt()
        val content = ArrayList<Any>()
        for (i in 0 until len) {
            content.add(innerReadMethods[innerIndex](moduleName, stream, innerIndex + 1))
        }
        return content
    }

    private fun readObject(moduleName: String, stream: ByteArrayReader): Any {
        val classId = stream.readInt()
        if (classId == NULL_IDENTIFIER) {
            return ""
        }
        return D2OUtil.getClassDefinition(moduleName, classId).read(moduleName, stream)
    }

}