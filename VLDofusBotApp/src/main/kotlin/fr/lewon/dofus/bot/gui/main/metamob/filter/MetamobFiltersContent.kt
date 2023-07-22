package fr.lewon.dofus.bot.gui.main.metamob.filter

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.ParameterLine
import fr.lewon.dofus.bot.gui.custom.darkGrayBoxStyle
import fr.lewon.dofus.bot.gui.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui.main.metamob.MetamobHelperUIUtil
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues

@Composable
fun MetamobFiltersContent() {
    Column(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle()) {
        Row(Modifier.fillMaxWidth().height(30.dp).darkGrayBoxStyle()) {
            CommonText(
                "Filters",
                modifier = Modifier.padding(horizontal = 10.dp).align(Alignment.CenterVertically),
                fontWeight = FontWeight.SemiBold
            )
        }
        Column(Modifier.padding(10.dp)) {
            val filterValues = MetamobHelperUIUtil.getUiStateValue().filterValues
            for (filter in MonsterFilters) {
                FilterLine(filter, filterValues)
            }
        }
        Spacer(Modifier.fillMaxHeight().weight(1f))
        Row(Modifier.padding(5.dp)) {
            Spacer(Modifier.fillMaxWidth().weight(1f))
            CommonText("Total displayed : ${MetamobHelperUIUtil.getFilteredMonsters().size}")
        }
    }
}

@Composable
private fun <T> FilterLine(filter: MonsterFilter<T>, filterValues: ParameterValues) {
    ParameterLine(
        filter.parameter,
        filterValues,
        onParamUpdate = { MetamobHelperUIUtil.updateFilter(filter, it) },
        modifier = Modifier.padding(vertical = 2.dp)
    )
}