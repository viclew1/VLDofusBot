package fr.lewon.dofus.bot.scripts.runeforge.strategies

import fr.lewon.dofus.bot.scripts.runeforge.DofusCharacteristic
import fr.lewon.dofus.bot.scripts.runeforge.RuneForgeLine
import fr.lewon.dofus.bot.scripts.runeforge.RuneForgeStrategy

class TmotivArmBandStrategy : RuneForgeStrategy() {

    override fun checkEnd(runeForgeLines: Map<DofusCharacteristic, RuneForgeLine>): Boolean {
        val apLine = runeForgeLines[DofusCharacteristic.AP]
        return apLine != null && apLine.current == 1
    }

    override fun getRuneToPass(runeForgeLines: Map<DofusCharacteristic, RuneForgeLine>): Pair<Int, DofusCharacteristic> {
        val criLine = runeForgeLines[DofusCharacteristic.CRITICAL] ?: error("No crit line found")
        if (criLine.current < criLine.max) {
            return Pair(0, DofusCharacteristic.CRITICAL)
        }
        val vitalityLine = runeForgeLines[DofusCharacteristic.VITALITY] ?: error("No vitality line found")
        if (vitalityLine.current <= vitalityLine.max - 50) return Pair(2, DofusCharacteristic.VITALITY)
        if (vitalityLine.current <= vitalityLine.max - 25) return Pair(1, DofusCharacteristic.VITALITY)
        val lowestDamage = listOf(
            DofusCharacteristic.FIRE_DAMAGE,
            DofusCharacteristic.WATER_DAMAGE,
            DofusCharacteristic.EARTH_DAMAGE,
            DofusCharacteristic.AIR_DAMAGE
        ).map { runeForgeLines[it] ?: error("No ${it.caracName} line found") }
            .minBy { it.current } ?: error("Couldn't find line to upgrade")
        if (lowestDamage.current < 5) {
            return Pair(0, lowestDamage.charac)
        }
        val powerLine = runeForgeLines[DofusCharacteristic.POWER] ?: error("No power line found")
        val power = powerLine.current
        if (power <= powerLine.max - 10) return Pair(2, DofusCharacteristic.POWER)
        if (power <= powerLine.max - 3) return Pair(1, DofusCharacteristic.POWER)
        return (Pair(0, DofusCharacteristic.AP))
    }

    override fun getRuneForgeLines(): List<RuneForgeLine> {
        return listOf(
            RuneForgeLine(81, 120, DofusCharacteristic.VITALITY),
            RuneForgeLine(21, 30, DofusCharacteristic.POWER),
            RuneForgeLine(6, 4, DofusCharacteristic.CRITICAL),
            RuneForgeLine(4, 6, DofusCharacteristic.EARTH_DAMAGE),
            RuneForgeLine(4, 6, DofusCharacteristic.FIRE_DAMAGE),
            RuneForgeLine(4, 6, DofusCharacteristic.WATER_DAMAGE),
            RuneForgeLine(4, 6, DofusCharacteristic.AIR_DAMAGE),
            RuneForgeLine(0, 1, DofusCharacteristic.AP)
        )
    }
}