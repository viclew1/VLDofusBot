package fr.lewon.dofus.bot.gui.vldb.panes.character.card.edit.spells

import fr.lewon.dofus.bot.core.d2o.managers.spell.SpellVariantManager
import fr.lewon.dofus.bot.core.model.spell.DofusSpell
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.vldb.panes.character.card.edit.spells.visual.SpellVisualPanel
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell
import fr.lewon.dofus.bot.util.filemanagers.impl.SpellAssetManager
import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.Toolkit
import javax.swing.*
import javax.swing.border.LineBorder

class SpellCellPanel(
    var characterSpell: CharacterSpell,
    private val spellVisualPanel: SpellVisualPanel
) : JPanel(MigLayout("insets 0")) {

    private val backgroundLabel = JLabel(ImageIcon())
    var isSelected = false
        set(value) {
            field = value
            updateBorderAndBackground()
        }


    init {
        background = AppColors.DEFAULT_UI_COLOR
        add(backgroundLabel, "w max, h max, al center")
        updateBorderAndBackground()
        updateSpellIcon()
    }

    private fun updateBorderAndBackground() {
        if (isSelected) {
            border = LineBorder(Color.WHITE)
            background = Color.LIGHT_GRAY
        } else {
            border = LineBorder(Color.BLACK)
            background = AppColors.DEFAULT_UI_COLOR
        }
    }

    private fun updateSpellIcon() {
        val newIcon = characterSpell.spellId?.let {
            ImageIcon(Toolkit.getDefaultToolkit().createImage(SpellAssetManager.getIconData(it)))
        }
        backgroundLabel.icon = newIcon
    }

    fun updatePopupMenu(breedId: Int) {
        val availableSpellsByName = getAvailableSpellsByName(breedId)
        val popupMenu = JPopupMenu()
        val emptyMenuItem = JMenuItem("")
        emptyMenuItem.addActionListener { updateSpell(null) }
        popupMenu.add(emptyMenuItem)
        for (entry in availableSpellsByName.entries.sortedBy { it.key.lowercase() }) {
            val menuItem = JMenuItem(entry.key)
            menuItem.addActionListener { updateSpell(entry.value) }
            popupMenu.add(menuItem)
        }
        componentPopupMenu = popupMenu
    }

    private fun updateSpell(spell: DofusSpell?) {
        this.characterSpell.spellId = spell?.id
        updateSpellIcon()
        if (isSelected) {
            spellVisualPanel.visualizeSpell(spell)
        }
    }

    private fun getAvailableSpellsByName(breedId: Int): Map<String, DofusSpell> {
        return SpellVariantManager.getSpellVariants(breedId)
            .flatMap { it.spells }
            .filter { it.levels.lastOrNull()?.effects?.isNotEmpty() ?: false }
            .associateBy { it.name }
    }

}