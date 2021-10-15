package fr.lewon.dofus.bot.gui.tabs.characters.form

import fr.lewon.dofus.bot.gui.tabs.characters.form.spells.SpellTable
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination
import net.miginfocom.swing.MigLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane

class CharacterFormAiPanel(w: Int, h: Int, character: DofusCharacter = DofusCharacter()) : JPanel() {

    private val table = SpellTable(character.spells.map { it.copy() })
    private var deleteButton = JButton("Delete").also { it.addActionListener { deleteSpells() } }
    private var createButton = JButton("Create").also { it.addActionListener { createSpell() } }

    init {
        layout = null
        setSize(w, h)
        table.showHorizontalLines = true
        table.showVerticalLines = true
        val tablePane = JScrollPane(table)
        tablePane.setBounds(0, 0, w, h - 50)
        add(tablePane)

        val buttonsPane = JPanel(MigLayout())
        buttonsPane.setBounds(0, h - 50, w, 50)
        buttonsPane.add(deleteButton)
        buttonsPane.add(JPanel(), "width max")
        buttonsPane.add(createButton)
        add(buttonsPane)
    }

    private fun createSpell() {
        val spell = SpellCombination()
        table.createSpell(spell)
    }

    private fun deleteSpells() {
        while (table.selectedRow != -1) {
            val index = table.selectedRow
            table.deleteSpell(index)
        }
    }

    fun getSpells(): List<SpellCombination> {
        return table.getSpells()
    }

}