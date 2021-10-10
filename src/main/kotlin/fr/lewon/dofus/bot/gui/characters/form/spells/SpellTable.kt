package fr.lewon.dofus.bot.gui.characters.form.spells

import fr.lewon.dofus.bot.gui.characters.form.spells.renderers.BooleanCellRenderer
import fr.lewon.dofus.bot.gui.characters.form.spells.renderers.IntCellRenderer
import fr.lewon.dofus.bot.gui.characters.form.spells.renderers.StringCellRenderer
import fr.lewon.dofus.bot.gui.custom.CustomJTextField
import fr.lewon.dofus.bot.gui.custom.IntegerJTextField
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination
import fr.lewon.dofus.bot.model.characters.spells.SpellType
import javax.swing.*

class SpellTable(spells: List<SpellCombination>) : JTable() {

    init {
        model = SpellTableModel(spells)
        autoResizeMode = AUTO_RESIZE_ALL_COLUMNS

        setDefaultRenderer(Boolean::class.java, BooleanCellRenderer())
        setDefaultEditor(Boolean::class.java, DefaultCellEditor(JCheckBox().also {
            it.background = background
            it.horizontalAlignment = SwingConstants.CENTER
        }))

        setDefaultEditor(SpellType::class.java, DefaultCellEditor(JComboBox(SpellType.values())))

        setDefaultEditor(Int::class.java, DefaultCellEditor(IntegerJTextField()))
        setDefaultRenderer(Int::class.java, IntCellRenderer())

        setDefaultRenderer(String::class.java, StringCellRenderer())
        setDefaultEditor(String::class.java, DefaultCellEditor(CustomJTextField()))
    }

    fun createSpell(spell: SpellCombination) {
        (model as SpellTableModel).addSpell(spell)
    }

    fun deleteSpell(index: Int) {
        (model as SpellTableModel).removeRow(index)
    }

    fun getSpells(): List<SpellCombination> {
        return (model as SpellTableModel).getSpells()
    }

}