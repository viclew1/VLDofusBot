package fr.lewon.dofus.bot.gui2.main.metamob.filter

import fr.lewon.dofus.bot.gui2.main.metamob.model.MetamobMonsterType
import fr.lewon.dofus.bot.gui2.util.AppFonts
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.util.StringUtil
import net.miginfocom.swing.MigLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane

object MonsterFilterPanel : JPanel(MigLayout()) {

    private val monsterNameParameter = DofusBotParameter(key = "Monster", description = "Monster name")
    private val monsterNameFilter = MonsterFilter(monsterNameParameter) { value, monster ->
        value.isEmpty() || StringUtil.removeAccents(monster.name).contains(StringUtil.removeAccents(value), true)
    }
    private val ownedParameter = DofusBotParameter(
        key = "Owned",
        description = "Owned monsters",
        defaultValue = OwnedParameterValues.ANY.label,
        type = DofusBotParameterType.CHOICE,
        possibleValues = OwnedParameterValues.values().map { it.label })
    private val ownedFilter = MonsterFilter(ownedParameter) { value, monster ->
        OwnedParameterValues.fromLabel(value).monsterMatchesFun(monster)
    }
    private val searchStatusParameter = DofusBotParameter(
        key = "Search status",
        description = "Monsters search statues",
        defaultValue = SearchedParameterValues.ANY.label,
        type = DofusBotParameterType.CHOICE,
        possibleValues = SearchedParameterValues.values().map { it.label })
    private val searchStatusFilter = MonsterFilter(searchStatusParameter) { value, monster ->
        SearchedParameterValues.fromLabel(value).monsterMatchesFun(monster)
    }
    private val typeParameter = DofusBotParameter(
        key = "Type",
        description = "Monsters type",
        defaultValue = MetamobMonsterType.ANY.displayLabel,
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


}