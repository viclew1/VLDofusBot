package fr.lewon.dofus.bot.gui2.main.metamob.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.custom.HorizontalSeparator
import fr.lewon.dofus.bot.gui2.custom.ParameterLine
import fr.lewon.dofus.bot.gui2.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui2.main.metamob.MetamobHelperUIUtil

@Composable
fun MetamobFiltersContent() {
    Column(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle()) {
        CommonText("Filters", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.SemiBold)
        HorizontalSeparator()
        Column(Modifier.padding(10.dp)) {
            for ((filter, value) in MetamobHelperUIUtil.uiState.value.valueByFilter) {
                ParameterLine(filter.parameter,
                    getParamValue = { value },
                    onParamUpdate = { MetamobHelperUIUtil.updateFilter(filter, it) }
                )
            }
        }
    }
}