package fr.lewon.dofus.bot.scripts.runeforge.strategies

import fr.lewon.dofus.bot.scripts.runeforge.DofusCharacteristic
import fr.lewon.dofus.bot.scripts.runeforge.RuneForgeLine
import fr.lewon.dofus.bot.scripts.runeforge.RuneForgeStrategy

class DrakeHeadStrategy : RuneForgeStrategy() {

    override fun checkEnd(runeForgeLines: Map<DofusCharacteristic, RuneForgeLine>): Boolean {
        val apLine = runeForgeLines[DofusCharacteristic.AP]
        return apLine != null && apLine.current == 1
    }

    override fun getRuneToPass(runeForgeLines: Map<DofusCharacteristic, RuneForgeLine>): Pair<Int, DofusCharacteristic> {
        val vitalityLine = runeForgeLines[DofusCharacteristic.VITALITY] ?: error("No vitality line found")
        if (vitalityLine.current <= vitalityLine.max - 50) {
            return Pair(2, DofusCharacteristic.VITALITY)
        }
        val lowestRes = listOf(
            DofusCharacteristic.AIR_PER_RES,
            DofusCharacteristic.EARTH_PER_RES,
            DofusCharacteristic.FIRE_PER_RES,
            DofusCharacteristic.NEUTRAL_PER_RES,
            DofusCharacteristic.WATER_PER_RES
        ).map { runeForgeLines[it] ?: error("No ${it.caracName} line found") }
            .minBy { it.current } ?: error("Couldn't find line to upgrade")
        if (lowestRes.current < 11) {
            return Pair(0, lowestRes.charac)
        }
        return (Pair(0, DofusCharacteristic.AP))
    }

    override fun getRuneForgeLines(): List<RuneForgeLine> {
        return listOf(
            RuneForgeLine(151, 200, DofusCharacteristic.VITALITY),
            RuneForgeLine(6, 13, DofusCharacteristic.NEUTRAL_PER_RES),
            RuneForgeLine(6, 13, DofusCharacteristic.EARTH_PER_RES),
            RuneForgeLine(6, 13, DofusCharacteristic.FIRE_PER_RES),
            RuneForgeLine(6, 13, DofusCharacteristic.WATER_PER_RES),
            RuneForgeLine(6, 13, DofusCharacteristic.AIR_PER_RES),
            RuneForgeLine(0, 1, DofusCharacteristic.AP)
        )
    }
}