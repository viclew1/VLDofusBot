package fr.lewon.dofus.bot.sniffer.util

import java.io.StringWriter

object HexHelper {

    /** Returns a char representation of a byte into
     * @param x - Byte to convert.
     * @return Char containing the equivalent.
     */
    private fun nibbleToDigit(x: Int): Char {
        val c = (x and 0xf).toChar()
        return if (c.toInt() > 9) (c.toInt() - 10 + 'a'.toInt()).toChar() else (c + '0'.toInt()) // int to hex char
    }

    /** Returns a text representation of a byte.
     * @param b - Byte to convert.
     * @return String containing the hex equivalent.
     */
    private fun toString(b: Int): String {
        val sb = StringBuffer()
        sb.append(nibbleToDigit(b shr 4))
        sb.append(nibbleToDigit(b))
        return sb.toString()
    }

    /** Returns a text representation of a byte array.
     * @param bytes - Array of bytes to convert.
     * @return String containing the hex equivalent.
     */
    fun toString(bytes: ByteArray): String {
        val sw = StringWriter()
        val length = bytes.size
        if (length > 0) {
            for (i in 0 until length) {
                sw.write(toString(bytes[i].toInt()))
                if (i != length - 1) sw.write(" ")
            }
        }
        return sw.toString()
    }

}