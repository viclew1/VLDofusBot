package fr.lewon.dofus.bot.core.io.stream

object BooleanByteWrapper {

    fun getFlag(byte: Byte, pos: Int): Boolean {
        return when (pos) {
            0 -> byte.toInt() and 1 != 0
            1 -> byte.toInt() and 2 != 0
            2 -> byte.toInt() and 4 != 0
            3 -> byte.toInt() and 8 != 0
            4 -> byte.toInt() and 16 != 0
            5 -> byte.toInt() and 32 != 0
            6 -> byte.toInt() and 64 != 0
            7 -> byte.toInt() and 128 != 0
            else -> error("Bytebox overflow.")
        }
    }

}