package fr.lewon.dofus.bot.gui.main.pathbuilder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.sharp.RadioButtonChecked
import androidx.compose.material.icons.sharp.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.custom.list.CustomListContent
import fr.lewon.dofus.bot.gui.main.characters.CharacterActivityState
import fr.lewon.dofus.bot.gui.main.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.model.characters.paths.MapsPath
import fr.lewon.dofus.bot.model.characters.paths.SubPath
import fr.lewon.dofus.bot.util.filemanagers.impl.MapsPathsManager

private const val maxSubPathsLength = 5

@Composable
fun PathBuilderContent() {
    Row(Modifier.fillMaxSize()) {
        val uiState = PathsUiUtil.getUiState()
        val selectedPath = uiState.mapsPaths.firstOrNull { it.name == uiState.selectedPathName }
        Column(Modifier.width(200.dp)) {
            PathsListContent(uiState)
        }
        if (selectedPath == null) {
            CommonText(
                "No harvestable set selected",
                modifier = Modifier.fillMaxWidth().padding(10.dp).padding(top = 30.dp),
                fontWeight = FontWeight.Bold,
            )
        } else {
            PathEditorContent(uiState, selectedPath)
        }
    }
}

@Composable
private fun PathEditorContent(uiState: PathsUiState, selectedPath: MapsPath) {
    CustomStyledColumn(
        "Selected Path : ${selectedPath.name} (${selectedPath.subPaths.size} / $maxSubPathsLength Sub Paths)",
        Modifier.padding(5.dp).fillMaxSize()
    ) {
        CharacterSelectorContent(uiState)
        RegisterNewSubPathContent(uiState, selectedPath)
        if (selectedPath.subPaths.isNotEmpty()) {
            Row(Modifier.height(30.dp).darkGrayBoxStyle().fillMaxWidth()) {
                CommonText(
                    "Sub Paths",
                    modifier = Modifier.align(Alignment.CenterVertically).padding(start = 10.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
            CustomVerticalScrollable(Modifier.fillMaxSize()) {
                for (subPath in selectedPath.subPaths) {
                    SubPathContent(selectedPath, subPath, uiState)
                }
            }
        }
    }
}

@Composable
private fun RegisterNewSubPathContent(uiState: PathsUiState, selectedPath: MapsPath) {
    Row(Modifier.height(30.dp).padding(2.dp)) {
        val registering = uiState.registeredSubPathId != null
        CommonText(
            text = "Register a new sub path :",
            modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically)
        )
        Row(Modifier.align(Alignment.CenterVertically)) {
            val buttonEnabled =
                registering || uiState.selectedCharacterName != null && selectedPath.subPaths.size < maxSubPathsLength
            ButtonWithTooltip(
                {
                    if (registering) {
                        PathsUiUtil.registerSubPath(null)
                    } else {
                        PathsUiUtil.startRegisteringSubPath(selectedPath)
                    }
                },
                if (registering) "Stop registering" else "Start registering",
                if (registering) Icons.Sharp.Stop else Icons.Sharp.RadioButtonChecked,
                RectangleShape,
                width = 30.dp,
                enabled = buttonEnabled,
                iconColor = if (buttonEnabled) AppColors.RED else Color.DarkGray,
                hoverAnimation = false
            )
        }
    }
}

@Composable
private fun SubPathContent(selectedPath: MapsPath, subPath: SubPath, uiState: PathsUiState) {
    val maps = subPath.mapIds.map { MapManager.getDofusMap(it) }
    val isRegistering = uiState.registeredSubPathId == subPath.id
    val backgroundColor = if (isRegistering) Color.DarkGray else Color.Transparent
    Column(Modifier.padding(5.dp).fillMaxWidth().darkGrayBoxStyle().background(backgroundColor)) {
        Row(Modifier.height(30.dp)) {
            Switch(subPath.enabled, {
                MapsPathsManager.updateSubPath(selectedPath.name, subPath.id, subPath.copy(enabled = it))
            }, modifier = Modifier.align(Alignment.CenterVertically))
            SimpleTextField(
                subPath.name,
                onValueChange = {
                    MapsPathsManager.updateSubPath(selectedPath.name, subPath.id, subPath.copy(name = it))
                },
                modifier = Modifier.width(200.dp).padding(vertical = 2.dp, horizontal = 10.dp)
                    .align(Alignment.CenterVertically),
                placeHolderText = "Define sub path name"
            )
            if (isRegistering) {
                Icon(
                    Icons.Default.RadioButtonChecked,
                    "",
                    modifier = Modifier.padding(horizontal = 5.dp).align(Alignment.CenterVertically),
                    tint = AppColors.RED
                )
                CommonText(
                    "REGISTERING...",
                    enabledColor = AppColors.RED,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            } else {
                CommonText(
                    "Maps : ${maps.size}",
                    modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 5.dp)
                )
            }
            Spacer(Modifier.fillMaxWidth().weight(1f))
            if (!isRegistering) {
                ButtonWithTooltip(
                    onClick = {
                        MapsPathsManager.updatePath(
                            selectedPath.name,
                            selectedPath.copy(subPaths = selectedPath.subPaths.minus(subPath))
                        )
                    },
                    title = "Delete sub path",
                    imageVector = Icons.Default.Close,
                    shape = RectangleShape,
                    width = 30.dp,
                    iconColor = AppColors.RED
                )
            }
        }
        Column(Modifier.padding(5.dp)) {
            for (map in maps) {
                CommonText(
                    map.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun CharacterSelectorContent(uiState: PathsUiState) {
    val initializedCharacters = CharactersUIUtil.getAllCharacterUIStates()
        .filter { it.activityState == CharacterActivityState.AVAILABLE || it.activityState == CharacterActivityState.BUSY }
        .map { it.name }
    val selectedCharacterName = uiState.selectedCharacterName?.takeIf { it in initializedCharacters }
        ?: initializedCharacters.firstOrNull()
    PathsUiUtil.setSelectedCharacterName(selectedCharacterName)
    val registering = uiState.registeredSubPathId != null
    Row(Modifier.padding(horizontal = 5.dp).height(30.dp)) {
        CommonText(
            "Character used for recording :",
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        if (selectedCharacterName != null) {
            Row(Modifier.width(200.dp).padding(5.dp)) {
                if (registering) {
                    CommonText(selectedCharacterName)
                } else {
                    ComboBox(
                        selectedItem = selectedCharacterName,
                        items = initializedCharacters,
                        onItemSelect = { PathsUiUtil.setSelectedCharacterName(it) },
                        getItemText = { it },
                        colors = ButtonDefaults.outlinedButtonColors(backgroundColor = AppColors.backgroundColor),
                    )
                }

            }
        } else {
            CommonText(
                "No initialized character",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun PathsListContent(uiState: PathsUiState) {
    val selectedPath = uiState.mapsPaths.firstOrNull { it.name == uiState.selectedPathName }
    CustomListContent(
        title = "Paths",
        emptyMessage = "No path available",
        selectedItems = selectedPath?.let { listOf(it) } ?: emptyList(),
        allItems = uiState.mapsPaths,
        canDeleteItem = { true },
        canSelectMultipleItems = false,
        onSelect = { PathsUiUtil.setSelectedPath(it.firstOrNull()?.name) },
        onDelete = { MapsPathsManager.deletePath(it.name) },
        canCreateItem = true,
        onCreate = {
            MapsPathsManager.addPath(it)
            PathsUiUtil.setSelectedPath(it)
        },
        createItemPlaceHolder = "New path name",
        createItemButtonText = "Create Path",
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