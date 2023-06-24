package fr.lewon.dofus.bot.core.model.spell

import fr.lewon.dofus.bot.core.VldbCoreInitializer
import fr.lewon.dofus.bot.core.fighter.IDofusFighter

data class DofusSpellTarget(val type: DofusSpellTargetType, val id: Int?, val casterOverwriteTarget: Boolean) {

    companion object {
        fun fromString(targetMask: String): List<DofusSpellTarget> {
            val targets = ArrayList<DofusSpellTarget>()
            for (subMask in targetMask.split(",")) {
                val casterOverwriteTarget = subMask[0] == '*'
                val key = if (casterOverwriteTarget) subMask[1] else subMask[0]
                val idOffset = if (casterOverwriteTarget) 2 else 1
                val type = DofusSpellTargetType.fromKey(key)
                    ?: if (VldbCoreInitializer.DEBUG) error("Invalid target : $subMask ($targetMask)") else continue
                val target = DofusSpellTarget(
                    type = type,
                    id = subMask.substring(idOffset).toIntOrNull(),
                    casterOverwriteTarget = casterOverwriteTarget
                )
                targets.add(target)
            }
            return targets
        }
    }

    fun canHitTarget(caster: IDofusFighter, target: IDofusFighter): Boolean {
        val realTarget = if (casterOverwriteTarget) caster else target
        return type.canHitTarget(id, caster, realTarget)
    }
}