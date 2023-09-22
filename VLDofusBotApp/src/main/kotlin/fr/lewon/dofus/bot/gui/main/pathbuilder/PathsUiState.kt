package fr.lewon.dofus.bot.gui.main.pathbuilder

import fr.lewon.dofus.bot.model.characters.paths.MapsPath
import fr.lewon.dofus.bot.util.filemanagers.impl.MapsPathsManager

data class PathsUiState(
    val selectedCharacterName: String? = null,
    val selectedPathName: String? = null,
    val mapsPaths: List<MapsPath> = MapsPathsManager.getPathByName().values.toList(),
    val registeredSubPathId: String? = null,
)