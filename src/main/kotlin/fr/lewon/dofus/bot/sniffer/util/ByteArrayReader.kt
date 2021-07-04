package fr.lewon.dofus.bot.sniffer.util

import java.nio.ByteBuffer
import java.nio.ByteOrder

class ByteArrayReader(private val byteArray: ByteArray) {

    var position = 0

    fun readNBytes(length: Int): ByteArray {
        val bytes = byteArray.copyOfRange(position, position + length)
        position += length
        return bytes
    }

    fun readVarShort(): Int {
        return readVar(2).toInt()
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

    fun readByte(order: ByteOrder = ByteOrder.BIG_ENDIAN): Byte {
        return getByteBuffer(1, order).get()
    }

    fun readBoolean(): Boolean {
        return readByte().toInt() != 0
    }

    fun readString(length: Int): String {
        return readNBytes(length).toString(Charsets.UTF_8)
    }

    fun readShort(order: ByteOrder = ByteOrder.BIG_ENDIAN): Int {
        return getByteBuffer(2, order).short.toInt()
    }

    fun readInt(order: ByteOrder = ByteOrder.BIG_ENDIAN): Int {
        return getByteBuffer(4, order).int
    }

    fun readDouble(order: ByteOrder = ByteOrder.BIG_ENDIAN): Double {
        return getByteBuffer(8, order).double
    }

    private fun getByteBuffer(length: Int, order: ByteOrder): ByteBuffer {
        return ByteBuffer.wrap(readNBytes(length)).order(order)
    }

    fun readUTF(): String {
        return readString(readShort())
    }

    fun skip(i: Int) {
        position += i
    }

    fun moveToPattern(pattern: ByteArray): Int {
        for (i in position..byteArray.size - pattern.size) {
            var correct = 0
            for (j in pattern.indices) {
                if (byteArray[i + j] == pattern[j]) {
                    correct++
                    if (correct == pattern.size) {
                        position = i
                        return i
                    }
                } else {
                    break
                }
            }
        }
        return -1
    }

    fun moveAfterPattern(pattern: ByteArray): Int {
        if (moveToPattern(pattern) == -1) {
            return -1
        }
        position += pattern.size
        return position
    }

}