package fr.lewon.dofus.bot.util.filemanagers.impl

import androidx.compose.ui.graphics.painter.Painter
import fr.lewon.dofus.bot.core.d2o.managers.characteristic.BreedManager
import fr.lewon.dofus.bot.core.d2o.managers.spell.SpellVariantManager
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.gui.util.toPainter
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager

object SpellAssetManager : ToInitManager {

    private lateinit var spellIconDataById: Map<Int, Painter>

    override fun initManager() {
        spellIconDataById = BreedManager.getAllBreeds()
            .flatMap { SpellVariantManager.getSpellVariants(it.id) }
            .flatMap { it.spells }
            .associate { it.id to buildPainter(it.iconId) }
    }

    override fun getNeededManagers(): List<ToInitManager> {
        return emptyList()
    }

    private fun buildPainter(iconId: Int): Painter {
        return javaClass.getResourceAsStream("/icon/spells/$iconId.png")?.readAllBytes()?.toPainter()
            ?: UiResource.UNKNOWN.imagePainter
    }

    fun getIconPainter(spellId: Int): Painter? {
        return spellIconDataById[spellId]
    }

}