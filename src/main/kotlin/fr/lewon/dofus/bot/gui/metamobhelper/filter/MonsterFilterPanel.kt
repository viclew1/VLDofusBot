package fr.lewon.dofus.bot.gui.metamobhelper.filter

import fr.lewon.dofus.bot.gui.metamobhelper.model.MetamobMonsterType
import fr.lewon.dofus.bot.gui.metamobhelper.monsters.MonsterListPanel
import fr.lewon.dofus.bot.gui.util.AppFonts
import fr.lewon.dofus.bot.gui.vldb.panes.script.parameters.ParametersPanel
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import net.miginfocom.swing.MigLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane

object MonsterFilterPanel : JPanel(MigLayout()) {

    private val monsterNameParameter = DofusBotParameter(key = "Monster", description = "Monster name")
    private val monsterNameFilter = MonsterFilter(monsterNameParameter) { value, monster ->
        value.isEmpty() || monster.name.contains(value, true)
    }
    private val ownedParameter = DofusBotParameter(
        key = "Owned",
        description = "Owned monsters",
        value = OwnedParameterValues.ANY.label,
        type = DofusBotParameterType.CHOICE,
        possibleValues = OwnedParameterValues.values().map { it.label })
    private val ownedFilter = MonsterFilter(ownedParameter) { value, monster ->
        OwnedParameterValues.fromLabel(value).monsterMatchesFun(monster)
    }
    private val searchStatusParameter = DofusBotParameter(
        key = "Search status",
        description = "Monsters search statues",
        value = SearchedParameterValues.ANY.label,
        type = DofusBotParameterType.CHOICE,
        possibleValues = SearchedParameterValues.values().map { it.label })
    private val searchStatusFilter = MonsterFilter(searchStatusParameter) { value, monster ->
        SearchedParameterValues.fromLabel(value).monsterMatchesFun(monster)
    }
    private val typeParameter = DofusBotParameter(
        key = "Type",
        description = "Monsters type",
        value = MetamobMonsterType.ANY.displayLabel,
        type = DofusBotParameterType.CHOICE,
        possibleValues = MetamobMonsterType.values().map { it.displayLabel })
    private val typeFilter = MonsterFilter(typeParameter) { value, monster ->
        val type = MetamobMonsterType.fromDisplayLabel(value)
        type == MetamobMonsterType.ANY || monster.type == type
    }

    val FILTERS = listOf(
        monsterNameFilter, ownedFilter, searchStatusFilter, typeFilter
    )

    private val filterLabel = JLabel("Filters").also { it.font = AppFonts.TITLE_FONT }
    private val filtersScrollPane = JScrollPane()

    init {
        add(filterLabel, "wrap")
        filtersScrollPane.horizontalScrollBar = null
        add(filtersScrollPane, "h max, width max, wrap")
        filtersScrollPane.setViewportView(ParametersPanel(FILTERS.map { it.parameter }) { _, _ ->
            MonsterListPanel.updateFilters()
        })
    }

}