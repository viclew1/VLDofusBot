package fr.lewon.dofus.bot.scripts.smithmagic

abstract class SmithMagicStrategy {

    abstract fun checkEnd(smithMagicLines: Map<String, SmithMagicLine>): Boolean

    abstract fun getRuneToPass(smithMagicLines: Map<String, SmithMagicLine>): Pair<Int, SmithMagicCharacteristics>

    abstract fun getSmithMagicType(): SmithMagicType

}