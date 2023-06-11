package fr.lewon.dofus.bot.core.model.interactive

import fr.lewon.dofus.bot.core.model.item.DofusItem
import fr.lewon.dofus.bot.core.model.job.DofusJob

data class DofusSkill(
    val skillId: Double,
    val elementActionId: Int,
    val label: String,
    val parentJob: DofusJob?,
    val gatheredResourceItem: DofusItem?,
    val levelMin: Int
)