package fr.lewon.dofus.bot.gui.main.exploration

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.AnimatedButton
import fr.lewon.dofus.bot.gui.main.exploration.map.ExplorationMapContent

@Composable
fun ExplorationHelperContent() {
    Box(Modifier.fillMaxSize()) {
        ExplorationMapContent()
        Row(Modifier.align(Alignment.CenterEnd)) {
            ConnectedCharactersContent()
            SelectedSubAreaContent()
        }
        AnimatedButton(
            { ExplorationUIUtil.setNextWorldMapHelper() },
            "Switch world map",
            Icons.Default.ChangeCircle,
            Modifier.width(200.dp).height(40.dp)
        )
    }
}