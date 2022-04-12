package fr.lewon.dofus.bot.util.filemanagers

import fr.lewon.dofus.bot.core.d2o.managers.characteristic.BreedManager
import fr.lewon.dofus.bot.core.d2o.managers.spell.SpellVariantManager

object SpellAssetManager {

    private lateinit var spellIconDataById: Map<Int, ByteArray?>

    fun initManager() {
        spellIconDataById = BreedManager.getAllBreeds()
            .flatMap { SpellVariantManager.getSpellVariants(it.id) }
            .flatMap { it.spells }
            .associate { it.id to buildIconData(it.iconId) }
    }

    private fun buildIconData(iconId: Int): ByteArray? {
        return javaClass.getResourceAsStream("/icon/spells/$iconId.png")?.readAllBytes()
    }

    fun getIconData(spellId: Int): ByteArray? {
        return spellIconDataById[spellId]
    }

}