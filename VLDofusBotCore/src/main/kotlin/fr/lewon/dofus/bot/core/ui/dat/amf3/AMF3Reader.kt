package fr.lewon.dofus.bot.core.ui.dat.amf3

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

class AMF3Reader {

    companion object {
        private const val REFERENCE_BIT = 0x01
        private val ZLIB_HEADER = byteArrayOf(0x78, 0x9c.toByte())
    }

    private val objects = ArrayList<Any>()
    private val classes = ArrayList<AMF3Class>()
    private val strings = ArrayList<String>()

    fun read(byteArray: ByteArray): Any? {
        return read(ByteArrayReader(byteArray))
    }

    private fun read(stream: ByteArrayReader): Any? {
        return read(stream, stream.readUnsignedByte())
    }

    private fun read(stream: ByteArrayReader, type: Int): Any? {
        return when (AMF3Type.fromInt(type)) {
            AMF3Type.NULL -> null
            AMF3Type.BOOL_FALSE -> false
            AMF3Type.BOOL_TRUE -> true
            AMF3Type.OBJECT -> readObject(stream)
            AMF3Type.INTEGER -> readInteger(stream)
            AMF3Type.NUMBER -> readNumber(stream)
            AMF3Type.DATE -> readDate(stream)
            AMF3Type.STRING -> readString(stream)
            AMF3Type.ARRAY -> readArray(stream)
            AMF3Type.BYTE_ARRAY -> readByteArray(stream)
        }
    }

    private fun readDate(stream: ByteArrayReader): Any {
        val ref = readInteger(stream, false)
        if (ref and REFERENCE_BIT == 0) {
            return objects[ref shr 1]
        }
        val ms = stream.readDouble()
        objects.add(ms)
        return ms
    }

    private fun readByteArray(stream: ByteArrayReader): Any {
        val ref = readInteger(stream, false)
        val size = ref shr 1
        if (ref and REFERENCE_BIT == 0) {
            return objects[size]
        }
        val buffer = stream.readNBytes(size)
        if (buffer.copyOfRange(0, 2).contentEquals(ZLIB_HEADER)) {
            return ByteArrayReader(buffer).uncompress()
        }
        return buffer
    }

    private fun readArray(stream: ByteArrayReader): Any {
        val ref = readInteger(stream, false)
        val size = ref shr 1
        if (ref and REFERENCE_BIT == 0) {
            return objects[size]
        }

        var key = readString(stream)
        if (key.isEmpty()) {
            val elements = (0 until size).map { read(stream) }
            objects.add(elements)
            return elements
        }
        val elements = HashMap<Any, Any?>()
        objects.add(elements)
        while (key.isNotEmpty()) {
            elements[key] = read(stream)
            key = readString(stream)
        }
        (0 until size).forEach { elements[it] = read(stream) }
        return elements
    }

    private fun decodeInt(stream: ByteArrayReader, signed: Boolean): Int {
        var n = 0
        var result = 0
        var b = stream.readUnsignedByte()
        while (b and 0x80 != 0 && n < 3) {
            result = result shl 7
            result = result or (b and 0x7f)
            b = stream.readUnsignedByte()
            n += 1
        }
        if (n < 3) {
            result = result shl 7
            result = result or b
        } else {
            result = result shl 8
            result = result or b
            if (result and 0x10000000 != 0) {
                if (signed) {
                    result -= 0x20000000
                } else {
                    result = result shl 1
                    result += 1
                }
            }
        }
        return result
    }

    private fun readObject(stream: ByteArrayReader): Any {
        val ref = readInteger(stream, false)
        val objectReference = ref shr 1
        if (ref and REFERENCE_BIT == 0) {
            return objects[objectReference]
        }

        val classDef = getClassDef(stream, objectReference)
        val objectAttributes = HashMap<String, Any?>()
        objects.add(objectAttributes)

        when (AMF3Encoding.fromInt(classDef.encoding)) {
            AMF3Encoding.EXTERNAL -> error("Untreated encoding : EXTERNAL")
            AMF3Encoding.DYNAMIC -> {
                readStatic(stream, classDef, objectAttributes)
                readDynamic(stream, objectAttributes)
            }
            AMF3Encoding.STATIC -> readStatic(stream, classDef, objectAttributes)
        }
        return objectAttributes
    }

    private fun readStatic(stream: ByteArrayReader, classDef: AMF3Class, objectAttributes: HashMap<String, Any?>) {
        for (attr in classDef.properties) {
            objectAttributes[attr] = read(stream)
        }
    }

    private fun readDynamic(stream: ByteArrayReader, objectAttributes: HashMap<String, Any?>) {
        var attr = readString(stream)
        while (attr.isNotEmpty()) {
            objectAttributes[attr] = read(stream)
            attr = readString(stream)
        }
    }

    private fun getClassDef(stream: ByteArrayReader, ref: Int): AMF3Class {
        val classReference = ref shr 1
        if (ref and REFERENCE_BIT == 0) {
            return classes[classReference]
        }
        val name = readString(stream)
        val encoding = classReference and 0x03
        val attrLen = classReference shr 2
        val classDef = AMF3Class(name, encoding, (0 until attrLen).map { readString(stream) })
        classes.add(classDef)
        return classDef
    }

    private fun readString(stream: ByteArrayReader): String {
        val ref = decodeInt(stream, false)
        val length = ref shr 1
        val isReference = ref and REFERENCE_BIT == 0
        if (isReference) {
            return strings[length]
        }
        if (length == 0) {
            return ""
        }
        val str = stream.readString(length)
        strings.add(str)
        return str
    }

    private fun readNumber(stream: ByteArrayReader): Double {
        return stream.readDouble()
    }

    private fun readInteger(stream: ByteArrayReader, signed: Boolean = true): Int {
        return decodeInt(stream, signed)
    }

}