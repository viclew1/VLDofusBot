package fr.lewon.dofus.bot.core.io.stream

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.util.zip.InflaterInputStream


class ByteArrayReader(byteArray: ByteArray) : DataInputStream(ByteArrayStream(byteArray)) {

    companion object {
        private const val MASK_10000000 = 128
        private const val MASK_01111111 = 127

        private const val CHUNK_BIT_SIZE = 7
        private const val LONG_SIZE = 64
        private const val INT_SIZE = 32
        private const val SHORT_SIZE = 16
    }

    private class ByteArrayStream(bytes: ByteArray) : ByteArrayInputStream(bytes) {
        fun getPosition(): Int {
            return pos
        }

        fun setPosition(pos: Int) {
            this.pos = pos
        }
    }

    fun getPosition(): Int {
        return (`in` as ByteArrayStream).getPosition()
    }

    fun setPosition(position: Int) {
        (`in` as ByteArrayStream).setPosition(position)
    }

    fun readString(length: Int): String {
        return readNBytes(length).toString(Charsets.UTF_8)
    }

    fun readVarShort(): Int {
        val shortValue = readVar(SHORT_SIZE).toInt()
        if (shortValue > Short.MAX_VALUE) return shortValue - UShort.MAX_VALUE.toInt() - 1
        return shortValue
    }

    fun readVarInt(): Int {
        return readVar(INT_SIZE).toInt()
    }

    fun readVarLong(): Long {
        return readVar(LONG_SIZE)
    }

    private fun readVar(size: Int): Long {
        var ans = 0L
        for (offset in 0 until size step CHUNK_BIT_SIZE) {
            val b = readUnsignedByte().toLong()
            ans += (b and MASK_01111111.toLong()) shl offset
            if (b and MASK_10000000.toLong() == 0L) {
                return ans
            }
        }
        error("Too much data")
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