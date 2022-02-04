package fr.lewon.dofus.bot.gui.panes.character.card.edit.spells

import fr.lewon.dofus.bot.core.model.spell.DofusSpell
import fr.lewon.dofus.bot.gui.custom.listrenderer.TextImageComboBox
import fr.lewon.dofus.bot.gui.custom.parameters.ParametersPanel
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell
import fr.lewon.dofus.bot.model.characters.spells.SpellType
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.DofusBotParameterType.*
import net.miginfocom.swing.MigLayout
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JScrollPane

class EditSpellPanel(var availableSpellsByName: Map<String, DofusSpell>) : JPanel(MigLayout()) {

    var currentSpell: CharacterSpell? = null

    private val spellTypeComboBox = TextImageComboBox(25, SpellType.values()).also { it.isVisible = false }
    private val spellParametersScrollPane = JScrollPane().also { it.border = BorderFactory.createEmptyBorder() }

    init {
        add(spellTypeComboBox, "w max, wrap")
        add(spellParametersScrollPane, "w max, h max")
    }

    fun updateCharacterSpell(spell: CharacterSpell?, onUpdate: () -> Unit) {
        currentSpell = spell
        updateTypeComboBox(onUpdate)
        if (spell?.type == SpellType.NAMED_SPELL) {
            if (!availableSpellsByName.values.contains(spell.spell)) {
                spell.spell = availableSpellsByName.values.minByOrNull { it.name }
                    ?: error("There should be at least one available spell")
            }
        }
        updateScrollPane(onUpdate)
    }

    private fun updateScrollPane(onUpdate: () -> Unit) {
        spellParametersScrollPane.setViewportView(buildParametersPane(onUpdate))
    }

    private fun updateTypeComboBox(onUpdate: () -> Unit) {
        val currentItemListeners = spellTypeComboBox.itemListeners.toList()
        currentItemListeners.forEach {
            spellTypeComboBox.removeItemListener(it)
        }
        spellTypeComboBox.addItemListener {
            currentSpell?.type = spellTypeComboBox.selectedItem as SpellType
            updateScrollPane(onUpdate)
            onUpdate()
        }
        spellTypeComboBox.isVisible = currentSpell != null
        spellTypeComboBox.selectedItem = currentSpell?.type ?: SpellType.NAMED_SPELL
    }

    private fun buildParametersPane(onUpdate: () -> Unit): JPanel {
        val parameters = getSpellParametersWithOnUpdate()
        return ParametersPanel(parameters) { p, v ->
            (p as SpellParameterWithOnUpdate).onUpdate(v)
            onUpdate()
        }
    }

    private fun getSpellParametersWithOnUpdate(): List<SpellParameterWithOnUpdate> {
        val characterSpell = currentSpell ?: return emptyList()
        return listOf(
            SpellParameterWithOnUpdate(
                "Keys",
                "Keys corresponding to the spell combination",
                characterSpell.key,
                STRING
            ) {
                characterSpell.key = it.toString()
            }
        ).union(
            when (characterSpell.type) {
                SpellType.NAMED_SPELL -> getNamedSpellSpecificSpellParameters()
                else -> getCustomSpellParametersWithOnUpdate()
            }
        ).toList()
    }

    private fun getNamedSpellSpecificSpellParameters(): List<SpellParameterWithOnUpdate> {
        val characterSpell = currentSpell ?: return emptyList()
        return listOf(
            SpellParameterWithOnUpdate(
                "Spell",
                "Spell from character class",
                characterSpell.spell.name,
                CHOICE,
                availableSpellsByName.keys.sorted()
            ) {
                characterSpell.spell = availableSpellsByName[it.toString()] ?: error("Spell not found : $it")
            },
        )
    }

    private fun getCustomSpellParametersWithOnUpdate(): List<SpellParameterWithOnUpdate> {
        val characterSpell = currentSpell ?: return emptyList()
        val spell = characterSpell.spell
        val spellLevel = spell.levels.lastOrNull() ?: return emptyList()
        return listOf(
            SpellParameterWithOnUpdate("Min range", "Minimal range", spellLevel.minRange.toString(), INTEGER) {
                spellLevel.minRange = it.toString().toInt()
            },
            SpellParameterWithOnUpdate("Max range", "Maximal range", spellLevel.maxRange.toString(), INTEGER) {
                spellLevel.maxRange = it.toString().toInt()
            },
            SpellParameterWithOnUpdate("Needs LOS", "Needs line of sight", spellLevel.castTestLos.toString(), BOOLEAN) {
                spellLevel.castTestLos = it.toString().toBoolean()
            },
            SpellParameterWithOnUpdate("Cast in line", "Cast in line", spellLevel.castInLine.toString(), BOOLEAN) {
                spellLevel.castInLine = it.toString().toBoolean()
            },
            SpellParameterWithOnUpdate(
                "Cast in diagonal", "Cast in diagonal", spellLevel.castInDiagonal.toString(), BOOLEAN
            ) {
                spellLevel.castInDiagonal = it.toString().toBoolean()
            },
            SpellParameterWithOnUpdate(
                "Modifiable range", "Modifiable range", spellLevel.rangeCanBeBoosted.toString(), BOOLEAN
            ) {
                spellLevel.rangeCanBeBoosted = it.toString().toBoolean()
            },
            SpellParameterWithOnUpdate("Cooldown", "Cooldown", spellLevel.minCastInterval.toString(), INTEGER) {
                spellLevel.minCastInterval = it.toString().toInt()
            },
            SpellParameterWithOnUpdate("AP cost", "AP cost", spellLevel.apCost.toString(), INTEGER) {
                spellLevel.apCost = it.toString().toInt()
            },
            SpellParameterWithOnUpdate(
                "Uses per turn", "Uses per turn", spellLevel.maxCastPerTurn.toString(), INTEGER
            ) {
                spellLevel.maxCastPerTurn = it.toString().toInt()
            },
            SpellParameterWithOnUpdate(
                "Uses per turn per target", "Uses per turn per target", spellLevel.maxCastPerTarget.toString(), INTEGER
            ) {
                spellLevel.maxCastPerTarget = it.toString().toInt()
            },
        ).union(
            when (characterSpell.type) {
                SpellType.CUSTOM_GAP_CLOSER -> getGapCloserSpecificSpellParameters()
                SpellType.CUSTOM_MP_BUFF -> getMpBuffSpecificSpellParameters()
                else -> emptyList()
            }
        ).toList()
    }

    private fun getGapCloserSpecificSpellParameters(): List<SpellParameterWithOnUpdate> {
        val characterSpell = currentSpell ?: return emptyList()
        val spell = characterSpell.spell
        val spellLevel = spell.levels.lastOrNull() ?: return emptyList()
        val spellEffect = spellLevel.effects.lastOrNull() ?: return emptyList()
        return listOf(
            SpellParameterWithOnUpdate(
                "Dash length",
                "Amount of cells traveled using the dash (only used if dash toward is set to true)",
                spellEffect.min.toString(),
                INTEGER
            ) {
                spellEffect.min = it.toString().toInt()
            },
            SpellParameterWithOnUpdate(
                "Needs hit",
                "Gap closer needs to hit a target",
                spellLevel.needTakenCell.toString(),
                BOOLEAN
            ) {
                spellLevel.needTakenCell = it.toString().toBoolean()
            },
        )
    }

    private fun getMpBuffSpecificSpellParameters(): List<SpellParameterWithOnUpdate> {
        val characterSpell = currentSpell ?: return emptyList()
        val spell = characterSpell.spell
        val spellLevel = spell.levels.lastOrNull() ?: return emptyList()
        val effect = spellLevel.effects.lastOrNull() ?: return emptyList()
        return listOf(
            SpellParameterWithOnUpdate("Amount", "Amount of MP given by the buff", effect.min.toString(), INTEGER) {
                effect.min = it.toString().toInt()
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