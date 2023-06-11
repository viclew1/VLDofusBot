package fr.lewon.dofus.bot.gui.main.jobs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CommonText

@Composable
fun JobsContent() {
    Row(Modifier.fillMaxSize()) {
        Row(Modifier.width(180.dp)) {
            HarvestableSetsList()
        }
        val selectedSetName = JobsUiUtil.selectedSetName.value
        if (selectedSetName == null) {
            CommonText(
                "No harvestable set selected",
                modifier = Modifier.fillMaxWidth().padding(10.dp).padding(top = 30.dp),
                fontWeight = FontWeight.Bold,
            )
        } else {
            SelectedHarvestableSetContent()
        }
    }
}