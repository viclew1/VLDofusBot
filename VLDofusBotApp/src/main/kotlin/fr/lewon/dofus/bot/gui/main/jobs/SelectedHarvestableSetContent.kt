package fr.lewon.dofus.bot.gui.main.jobs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.toPainter
import fr.lewon.dofus.bot.model.jobs.HarvestJobs
import fr.lewon.dofus.bot.util.filemanagers.impl.HarvestableSetsManager

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectedHarvestableSetContent() {
    val selectedSetName = JobsUiUtil.selectedSetName.value
    val toHarvestItemIds = JobsUiUtil.harvestableIdsBySetName.value[selectedSetName]
        ?: emptySet()
    VerticalGrid(columns = HarvestJobs.entries.size, items = HarvestJobs.entries) { job ->
        Column(Modifier.padding(5.dp).grayBoxStyle()) {
            Row(Modifier.height(30.dp).fillMaxWidth().darkGrayBoxStyle().padding(start = 10.dp)) {
                CommonText(
                    job.jobName,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    fontWeight = FontWeight.Bold
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(55.dp),
                modifier = Modifier.padding(5.dp).fillMaxHeight(),
            ) {
                items(job.items) { harvestable ->
                    val shouldHarvest = toHarvestItemIds.contains(harvestable.id)
                    val color = if (shouldHarvest) {
                        AppColors.primaryLightColor
                    } else {
                        Color.Red
                    }
                    val isEditable = !HarvestableSetsManager.defaultHarvestableIdsBySetName.contains(selectedSetName)
                    val modifier = if (isEditable && selectedSetName != null) {
                        Modifier.handPointerIcon().onClick {
                            if (shouldHarvest) {
                                HarvestableSetsManager.removeItemToHarvest(
                                    setName = selectedSetName,
                                    itemId = harvestable.id
                                )
                            } else {
                                HarvestableSetsManager.addItemToHarvest(
                                    setName = selectedSetName,
                                    itemId = harvestable.id
                                )
                            }
                            JobsUiUtil.harvestableIdsBySetName.value =
                                HarvestableSetsManager.getHarvestableIdsBySetName()
                        }
                    } else Modifier
                    TooltipTarget(
                        text = "${harvestable.name} (${harvestable.level})",
                        modifier = modifier.padding(3.dp).border(BorderStroke(1.dp, color)).padding(3.dp)
                    ) {
                        Surface(color = color.copy(alpha = 0.5f)) {
                            val gfxImageData = harvestable.cachedIcon
                            Image(gfxImageData.toPainter(), "")
                        }
                    }
                }
            }
        }
    }
}