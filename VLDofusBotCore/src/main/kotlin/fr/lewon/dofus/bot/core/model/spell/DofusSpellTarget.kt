package fr.lewon.dofus.bot.core.model.spell

import fr.lewon.dofus.bot.core.VldbCoreInitializer
import fr.lewon.dofus.bot.core.fighter.IDofusFighter

data class DofusSpellTarget(private val type: DofusSpellTargetType, private val id: Int?) {

    companion object {
        fun fromString(targetMask: String): List<DofusSpellTarget> {
            val targets = ArrayList<DofusSpellTarget>()
            for (subMask in targetMask.split(",")) {
                val type = DofusSpellTargetType.fromString(subMask[0])
                    ?: if (VldbCoreInitializer.DEBUG) error("Invalid target : $subMask ($targetMask)") else continue
                val target = DofusSpellTarget(type, subMask.substring(1).toIntOrNull())
                targets.add(target)
            }
            return targets
        }
    }

    fun canHitTarget(caster: IDofusFighter, target: IDofusFighter): Boolean {
        return type.canHitTarget(id, caster, target)
    }
}