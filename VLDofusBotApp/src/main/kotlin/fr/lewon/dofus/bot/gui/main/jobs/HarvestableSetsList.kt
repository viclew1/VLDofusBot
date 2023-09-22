package fr.lewon.dofus.bot.gui.main.jobs

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.SubTitleText
import fr.lewon.dofus.bot.gui.custom.list.CustomListContent
import fr.lewon.dofus.bot.util.filemanagers.impl.HarvestableSetsManager

@Composable
fun HarvestableSetsList() {
    val harvestableIdsBySetName = JobsUiUtil.harvestableIdsBySetName.value
    val selectedSetName = JobsUiUtil.selectedSetName.value
    CustomListContent(
        title = "Harvestable Sets",
        emptyMessage = "No harvestable set available",
        selectedItems = selectedSetName?.let { listOf(it) } ?: emptyList(),
        allItems = harvestableIdsBySetName.keys.toList(),
        canDeleteItem = { !HarvestableSetsManager.defaultHarvestableIdsBySetName.contains(it) },
        canSelectMultipleItems = false,
        onSelect = { JobsUiUtil.selectedSetName.value = it.firstOrNull() },
        onDelete = { JobsUiUtil.deleteSet(it) },
        canCreateItem = true,
        onCreate = {
            HarvestableSetsManager.addSet(it)
            JobsUiUtil.harvestableIdsBySetName.value = HarvestableSetsManager.getHarvestableIdsBySetName()
            JobsUiUtil.selectedSetName.value = it
        },
        createItemPlaceHolder = "New set name",
        createItemButtonText = "Create Set",
        itemCardMainContent = { item: String, textColor: Color ->
            SubTitleText(
                item,
                Modifier.align(Alignment.CenterVertically).padding(start = 5.dp),
                maxLines = 1,
                enabledColor = textColor
            )
        }
    )
}