package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.core.d2o.managers.characteristic.BreedManager
import fr.lewon.dofus.bot.core.d2o.managers.spell.SpellVariantManager
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager

object SpellAssetManager : ToInitManager {

    private lateinit var spellIconDataById: Map<Int, ByteArray?>

    override fun initManager() {
        spellIconDataById = BreedManager.getAllBreeds()
            .flatMap { SpellVariantManager.getSpellVariants(it.id) }
            .flatMap { it.spells }
            .associate { it.id to buildIconData(it.iconId) }
    }

    override fun getNeededManagers(): List<ToInitManager> {
        return emptyList()
    }

    private fun buildIconData(iconId: Int): ByteArray? {
        return javaClass.getResourceAsStream("/icon/spells/$iconId.png")?.readAllBytes()
    }

    fun getIconData(spellId: Int): ByteArray? {
        return spellIconDataById[spellId]
    }

}