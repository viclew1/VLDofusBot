package fr.lewon.dofus.bot.gui.vldb.panes.character.card.edit.spells.visual

import fr.lewon.dofus.bot.core.model.spell.DofusSpell
import net.miginfocom.swing.MigLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities

class SpellVisualPanel : JPanel(MigLayout()) {

    companion object {
        private const val AREA_VISUAL_SZ_WIDTH_RATIO = 0.9f
    }

    private val rangeLabel = JLabel()
    private val aoeLabel = JLabel()

    private val areaVisualPanel = AreaVisualPanel()

    init {
        SwingUtilities.invokeLater {
            add(rangeLabel, "al center, wrap")
            val areaSz = (AREA_VISUAL_SZ_WIDTH_RATIO * width).toInt()
            val areaVisualPanelConstraints = "w $areaSz:$areaSz:$areaSz, h $areaSz:$areaSz:$areaSz, al center, wrap"
            areaVisualPanel.setSize(areaSz, areaSz)
            areaVisualPanel.isVisible = false
            add(areaVisualPanel, areaVisualPanelConstraints)
            add(aoeLabel, "al center, wrap")
        }
    }

    fun visualizeSpell(spell: DofusSpell?) {
        SwingUtilities.invokeLater {
            val visible = spell != null
            rangeLabel.isVisible = visible
            aoeLabel.isVisible = visible
            areaVisualPanel.isVisible = visible
            val spellLevel = spell?.levels?.lastOrNull()
            val spellZone = spellLevel?.effects?.lastOrNull()?.rawZone
            if (spellLevel != null && spellZone != null) {
                rangeLabel.text = "Range : ${spellLevel.minRange} to ${spellLevel.maxRange}"
                if (spellLevel.rangeCanBeBoosted) {
                    rangeLabel.text += " (Modifiable)"
                }
                aoeLabel.text = "Area : ${spellZone.effectZoneType.name} (${spellZone.size})"
                areaVisualPanel.spell = spellLevel
            }
        }
    }

}