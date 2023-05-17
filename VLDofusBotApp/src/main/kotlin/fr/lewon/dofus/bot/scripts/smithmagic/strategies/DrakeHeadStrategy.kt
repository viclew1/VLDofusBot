package fr.lewon.dofus.bot.scripts.smithmagic.strategies

import fr.lewon.dofus.bot.scripts.smithmagic.SmithMagicCharacteristics
import fr.lewon.dofus.bot.scripts.smithmagic.SmithMagicLine
import fr.lewon.dofus.bot.scripts.smithmagic.SmithMagicStrategy
import fr.lewon.dofus.bot.scripts.smithmagic.SmithMagicType

class DrakeHeadStrategy : SmithMagicStrategy() {

    override fun checkEnd(smithMagicLines: Map<String, SmithMagicLine>): Boolean {
        val apLine = smithMagicLines[SmithMagicCharacteristics.AP.keyWord]
        return apLine != null && apLine.current == 1
    }

    override fun getRuneToPass(smithMagicLines: Map<String, SmithMagicLine>): Pair<Int, SmithMagicCharacteristics> {
        val vitalityLine =
            smithMagicLines[SmithMagicCharacteristics.VITALITY.keyWord] ?: error("No vitality line found")
        if (vitalityLine.current <= vitalityLine.max - 50) {
            return Pair(2, SmithMagicCharacteristics.VITALITY)
        }
        val lowestRes = listOf(
            SmithMagicCharacteristics.AIR_PER_RES,
            SmithMagicCharacteristics.EARTH_PER_RES,
            SmithMagicCharacteristics.FIRE_PER_RES,
            SmithMagicCharacteristics.NEUTRAL_PER_RES,
            SmithMagicCharacteristics.WATER_PER_RES
        ).map { it to (smithMagicLines[it.keyWord] ?: error("No ${it.keyWord} line found")) }
            .minByOrNull { it.second.current } ?: error("Couldn't find line to upgrade")
        if (lowestRes.second.current < 11) {
            return Pair(0, lowestRes.first)
        }
        return Pair(0, SmithMagicCharacteristics.AP)
    }

    override fun getSmithMagicType(): SmithMagicType {
        return SmithMagicType.COSTUMAGE
    }
}