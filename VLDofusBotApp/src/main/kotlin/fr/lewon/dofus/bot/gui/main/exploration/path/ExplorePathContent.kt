package fr.lewon.dofus.bot.gui.main.exploration.path

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.CustomStyledColumn
import fr.lewon.dofus.bot.gui.custom.SubTitleText
import fr.lewon.dofus.bot.gui.custom.list.CustomListContent
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.model.characters.paths.MapsPath
import fr.lewon.dofus.bot.util.filemanagers.impl.MapsPathsManager

@Composable
fun ExplorePathContent() {
    Column {
        Row(Modifier.height(250.dp)) {
            PathSelectorContent()
        }
        SelectedPathContent()
    }
}

@Composable
private fun PathSelectorContent() {
    CustomListContent(
        title = "Available paths",
        emptyMessage = "No path available, you can create them in the Path Builder screen",
        selectedItems = ExplorationUIUtil.selectedPath.value?.let(::listOf) ?: emptyList(),
        allItems = MapsPathsManager.getPathByName().values.toList(),
        canDeleteItem = { false },
        canSelectMultipleItems = false,
        onSelect = { ExplorationUIUtil.selectedPath.value = it.firstOrNull() },
        canCreateItem = false,
        itemCardMainContent = { path: MapsPath, textColor: Color ->
            SubTitleText(
                path.name,
                Modifier.align(Alignment.CenterVertically).padding(start = 5.dp),
                maxLines = 1,
                enabledColor = textColor
            )
        }
    )
}

@Composable
private fun SelectedPathContent() {
    val selectedPath = ExplorationUIUtil.selectedPath.value
    CustomStyledColumn(
        "Selected Path : ${selectedPath?.name ?: "/"}",
        modifier = Modifier.fillMaxSize().padding(5.dp)
    ) {
        Column(Modifier.padding(5.dp)) {
            if (selectedPath == null) {
                CommonText("No area selected", modifier = Modifier.fillMaxHeight().padding(start = 10.dp))
            } else {
                for (subPath in selectedPath.subPaths) {
                    val textColor = if (subPath.enabled) Color.White else Color.Gray
                    Row {
                        Checkbox(
                            subPath.enabled,
                            {
                                MapsPathsManager.updateSubPath(
                                    selectedPath.name,
                                    subPath.id,
                                    subPath.copy(enabled = it)
                                )
                            },
                            modifier = Modifier.height(30.dp).width(45.dp).align(Alignment.CenterVertically)
                        )
                        CommonText(
                            "${subPath.displayName} (${subPath.mapIds.size} maps)",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            enabledColor = textColor,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}