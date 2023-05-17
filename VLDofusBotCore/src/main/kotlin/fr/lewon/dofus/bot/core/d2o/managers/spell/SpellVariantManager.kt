package fr.lewon.dofus.bot.core.d2o.managers.spell

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.model.spell.DofusSpellVariant

object SpellVariantManager : VldbManager {

    private lateinit var variantsByBreedId: HashMap<Int, ArrayList<DofusSpellVariant>>

    override fun initManager() {
        variantsByBreedId = HashMap()
        D2OUtil.getObjects("SpellVariants").forEach {
            val id = it["id"].toString().toInt()
            val breedId = it["breedId"].toString().toInt()
            val spellIds = it["spellIds"] as List<Int>
            val spells = spellIds.mapNotNull { spellId -> SpellManager.getSpell(spellId) }
            val variants = variantsByBreedId.computeIfAbsent(breedId) { ArrayList() }
            variants.add(DofusSpellVariant(id, spells))
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return listOf(SpellManager)
    }

    fun getSpellVariants(breedId: Int): List<DofusSpellVariant> {
        return variantsByBreedId[breedId] ?: error("No variant for breed : $breedId")
    }

}