package fr.lewon.dofus.bot.scripts.smithmagic

class SmithMagicLine(val min: Int, val max: Int, val characteristicKeyWord: String, val order: Int = Int.MAX_VALUE) {
    var current: Int = 0
}