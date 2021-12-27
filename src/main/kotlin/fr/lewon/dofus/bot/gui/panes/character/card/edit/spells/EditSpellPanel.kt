package fr.lewon.dofus.bot.gui.panes.character.card.edit.spells

import fr.lewon.dofus.bot.gui.custom.listrenderer.TextImageComboBox
import fr.lewon.dofus.bot.gui.custom.parameters.ParametersPanel
import fr.lewon.dofus.bot.model.characters.spells.AreaType
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination
import fr.lewon.dofus.bot.model.characters.spells.SpellType
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.DofusBotParameterType.*
import net.miginfocom.swing.MigLayout
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JScrollPane

class EditSpellPanel : JPanel(MigLayout()) {

    private val spellTypeComboBox = TextImageComboBox(25, SpellType.values()).also { it.isVisible = false }
    private val spellParametersScrollPane = JScrollPane().also { it.border = BorderFactory.createEmptyBorder() }

    init {
        add(spellTypeComboBox, "w max, wrap")
        add(spellParametersScrollPane, "w max, h max")
    }

    fun updateSpellCombination(spell: SpellCombination?, onUpdate: () -> Unit) {
        updateTypeComboBox(spell, onUpdate)
        updateScrollPane(spell, onUpdate)
    }

    private fun updateScrollPane(spell: SpellCombination?, onUpdate: () -> Unit) {
        spellParametersScrollPane.setViewportView(buildParametersPane(spell, onUpdate))
    }

    private fun updateTypeComboBox(spell: SpellCombination?, onUpdate: () -> Unit) {
        val currentItemListeners = spellTypeComboBox.itemListeners.toList()
        currentItemListeners.forEach {
            spellTypeComboBox.removeItemListener(it)
        }
        spellTypeComboBox.addItemListener {
            spell?.type = spellTypeComboBox.selectedItem as SpellType
            updateScrollPane(spell, onUpdate)
            onUpdate()
        }
        spellTypeComboBox.isVisible = spell != null
        spellTypeComboBox.selectedItem = spell?.type ?: SpellType.ATTACK
    }

    private fun buildParametersPane(spell: SpellCombination?, onUpdate: () -> Unit): JPanel {
        val parameters = getSpellParametersWithOnUpdate(spell)
        return ParametersPanel(parameters) { p, v ->
            (p as SpellParameterWithOnUpdate).onUpdate(v)
            onUpdate()
        }
    }

    private fun getSpellParametersWithOnUpdate(spell: SpellCombination?): List<SpellParameterWithOnUpdate> {
        spell ?: return emptyList()
        return listOf(
            SpellParameterWithOnUpdate("Keys", "Keys corresponding to the spell combination", spell.keys, STRING) {
                spell.keys = it.toString()
            },
            SpellParameterWithOnUpdate("Min range", "Minimal range", spell.minRange.toString(), INTEGER) {
                spell.minRange = it.toString().toInt()
            },
            SpellParameterWithOnUpdate("Max range", "Maximal range", spell.maxRange.toString(), INTEGER) {
                spell.maxRange = it.toString().toInt()
            },
            SpellParameterWithOnUpdate("Needs LOS", "Needs line of sight", spell.needsLos.toString(), BOOLEAN) {
                spell.needsLos = it.toString().toBoolean()
            },
            SpellParameterWithOnUpdate("Cast in line", "Cast in line", spell.castInLine.toString(), BOOLEAN) {
                spell.castInLine = it.toString().toBoolean()
            },
            SpellParameterWithOnUpdate(
                "Cast in diagonal", "Cast in diagonal", spell.castInDiagonal.toString(), BOOLEAN
            ) {
                spell.castInDiagonal = it.toString().toBoolean()
            },
            SpellParameterWithOnUpdate(
                "Modifiable range", "Modifiable range", spell.modifiableRange.toString(), BOOLEAN
            ) {
                spell.modifiableRange = it.toString().toBoolean()
            },
            SpellParameterWithOnUpdate("Cooldown", "Cooldown", spell.cooldown.toString(), INTEGER) {
                spell.cooldown = it.toString().toInt()
            },
            SpellParameterWithOnUpdate("AP cost", "AP cost", spell.apCost.toString(), INTEGER) {
                spell.apCost = it.toString().toInt()
            },
            SpellParameterWithOnUpdate("Uses per turn", "Uses per turn", spell.usesPerTurn.toString(), INTEGER) {
                spell.usesPerTurn = it.toString().toInt()
            },
            SpellParameterWithOnUpdate(
                "Uses per turn per target", "Uses per turn per target", spell.usesPerTurnPerTarget.toString(), INTEGER
            ) {
                spell.usesPerTurnPerTarget = it.toString().toInt()
            },
        ).union(
            when (spell.type) {
                SpellType.ATTACK -> getAttackSpecificSpellParameters(spell)
                SpellType.GAP_CLOSER -> getGapCloserSpecificSpellParameters(spell)
                SpellType.MP_BUFF -> getMpBuffSpecificSpellParameters(spell)
            }
        ).toList()
    }

    private fun getAttackSpecificSpellParameters(spell: SpellCombination): List<SpellParameterWithOnUpdate> {
        return listOf(
            SpellParameterWithOnUpdate(
                "AI weight", "The bot will prefer using the spells with the highest AI weight",
                spell.aiWeight.toString(), INTEGER
            ) {
                spell.aiWeight = it.toString().toInt()
            },
            SpellParameterWithOnUpdate("AOE size", "AOE size", spell.areaSize.toString(), INTEGER) {
                spell.areaSize = it.toString().toInt()
            },
            SpellParameterWithOnUpdate(
                "AOE type", "AOE type", spell.areaType.name, CHOICE, AreaType.values().map { it.name }.toList()
            ) {
                spell.areaType = AreaType.valueOf(it.toString())
            },
            SpellParameterWithOnUpdate(
                "AOE needs hit", "AOE needs to be casted on a target", spell.areaNeedsHit.toString(), BOOLEAN
            ) {
                spell.areaNeedsHit = it.toString().toBoolean()
            },
        )
    }

    private fun getGapCloserSpecificSpellParameters(spell: SpellCombination): List<SpellParameterWithOnUpdate> {
        return listOf()
    }

    private fun getMpBuffSpecificSpellParameters(spell: SpellCombination): List<SpellParameterWithOnUpdate> {
        return listOf(
            SpellParameterWithOnUpdate("Amount", "Amount of MP given by the buff", spell.amount.toString(), INTEGER) {
                spell.amount = it.toString().toInt()
            }
        )
    }

    private class SpellParameterWithOnUpdate(
        key: String,
        description: String,
        value: String,
        type: DofusBotParameterType,
        possibleValues: List<String> = emptyList(),
        val onUpdate: (Any) -> Unit
    ) : DofusBotParameter(key, description, value, type, possibleValues)

}