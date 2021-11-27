package fr.lewon.dofus.bot.gui.tabs.characters.form.spells

import fr.lewon.dofus.bot.model.characters.spells.SpellCombination
import fr.lewon.dofus.bot.model.characters.spells.SpellType
import javax.swing.table.DefaultTableModel

class SpellTableModel(spells: List<SpellCombination>) : DefaultTableModel() {

    private val columnsWithTypes = listOf(
        ColumnInfo("Type", SpellType::class.java) { it.type },
        ColumnInfo("Keys", String::class.java) { it.keys },
        ColumnInfo("Min range", Int::class.java) { it.minRange },
        ColumnInfo("Max range", Int::class.java) { it.maxRange },
        ColumnInfo("Needs LOS", Boolean::class.java) { it.needsLos },
        ColumnInfo("Cast in line", Boolean::class.java) { it.castInLine },
        ColumnInfo("Modifiable range", Boolean::class.java) { it.modifiableRange },
        ColumnInfo("Cooldown", Int::class.java) { it.cooldown },
        ColumnInfo("AP cost", Int::class.java) { it.apCost },
        ColumnInfo("Uses per turn", Int::class.java) { it.usesPerTurn },
        ColumnInfo("Amount", Int::class.java) { it.amount },
        ColumnInfo("AI Weight", Int::class.java) { it.aiWeight },
    )

    init {
        setColumnIdentifiers(columnsWithTypes.map { it.colName }.toTypedArray())
        spells.forEach { addRow(buildRow(it)) }
    }

    private fun buildRow(spell: SpellCombination): Array<*> {
        return columnsWithTypes.map { it.valueGetter(spell) }.toTypedArray()
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return columnsWithTypes[columnIndex].type
    }

    fun getSpells(): List<SpellCombination> {
        val columnTypes = columnsWithTypes.map { it.type }.toTypedArray()
        val constructor = SpellCombination::class.java.getDeclaredConstructor(*columnTypes)
        return dataVector.map { constructor.newInstance(*parseValues(it.toArray())) }
    }

    private fun parseValues(values: Array<Any>): Array<Any> {
        return values.withIndex().map { parseValue(columnsWithTypes[it.index].type, it.value) }.toTypedArray()
    }

    private fun parseValue(type: Class<*>, value: Any): Any {
        if (value is String) {
            return when (type) {
                Int::class.java -> value.toIntOrNull() ?: 0
                else -> value
            }
        }
        return value
    }

    fun addSpell(spell: SpellCombination) {
        addRow(buildRow(spell))
    }

    private class ColumnInfo(
        val colName: String,
        val type: Class<*>,
        val valueGetter: (SpellCombination) -> Any
    )
}