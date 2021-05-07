package fr.lewon.dofus.bot.scripts.runeforge

abstract class RuneForgeStrategy {

    abstract fun checkEnd(runeForgeLines: Map<DofusCharacteristic, RuneForgeLine>): Boolean

    abstract fun getRuneToPass(runeForgeLines: Map<DofusCharacteristic, RuneForgeLine>): Pair<Int, DofusCharacteristic>

    abstract fun getRuneForgeLines(): List<RuneForgeLine>

}