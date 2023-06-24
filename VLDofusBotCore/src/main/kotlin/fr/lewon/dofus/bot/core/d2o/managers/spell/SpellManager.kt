package fr.lewon.dofus.bot.core.d2o.managers.spell

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.model.spell.DofusSpell

object SpellManager : VldbManager {

    private lateinit var spellById: Map<Int, DofusSpell>

    override fun initManager() {
        spellById = D2OUtil.getObjects("Spells").associate {
            val id = it["id"].toString().toInt()
            val iconId = it["iconId"].toString().toInt()
            val name = I18NUtil.getLabel(it["nameId"].toString().toInt()) ?: "UNKNOWN_SPELL_NAME"
            val levelIds = it["spellLevels"] as List<Int>
            val levels = levelIds.map { lvlId -> SpellLevelManager.getSpellLevel(lvlId) }
            val adminName = it["adminName"].toString()
            id to DofusSpell(id, iconId, name, levels, adminName)
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return listOf(SpellLevelManager)
    }

    fun getSpell(id: Int): DofusSpell? {
        return spellById[id]
    }

}