package fr.lewon.dofus.bot.util.io.stream

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.util.zip.InflaterInputStream


class ByteArrayReader(byteArray: ByteArray) : DataInputStream(ByteArrayInputStream(byteArray)) {


    init {
        mark(Int.MAX_VALUE)
    }

    fun setPosition(position: Int) {
        reset()
        mark(Int.MAX_VALUE)
        skip(position)
    }

    fun readString(length: Int): String {
        return readNBytes(length).toString(Charsets.UTF_8)
    }

    fun readVarShort(): Int {
        val shortValue = readVar(2).toInt()
        if (shortValue > Short.MAX_VALUE) return shortValue - UShort.MAX_VALUE.toInt() - 1
        return shortValue
    }

    fun readVarInt(): Int {
        return readVar(4).toInt()
    }

    fun readVarLong(): Long {
        return readVar(8)
    }

    private fun readVar(size: Int): Long {
        var ans = 0L
        for (i in 0 until 8 * size step 7) {
            val b = readByte()
            ans += (b.toLong() and 0b01111111) shl i
            if (b.toLong() and 0b10000000 == 0L)
                return ans
        }
        return -1
    }

    fun skip(i: Int) {
        skip(i.toLong())
    }

    fun uncompress(bufferSize: Int = 8192): ByteArray {
        val baos = ByteArrayOutputStream()
        val gzip = InflaterInputStream(ByteArrayInputStream(readNBytes(available())))
        val buffer = ByteArray(bufferSize)
        var n: Int
        while (gzip.read(buffer).also { n = it } >= 0) {
            baos.write(buffer, 0, n)
        }
        return baos.toByteArray()
    }

}