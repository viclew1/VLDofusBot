package fr.lewon.dofus.bot.gui.main.scripts.scripts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.handPointerIcon
import fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.ScriptTabContent
import fr.lewon.dofus.bot.gui.util.AppColors

@Composable
fun ScriptsTabsContent() {
    val currentPage = ScriptTabsUIUtil.getCurrentTab()
    Column {
        TabRow(
            selectedTabIndex = currentPage.ordinal,
            backgroundColor = MaterialTheme.colors.background,
            contentColor = AppColors.primaryLightColor,
            modifier = Modifier.height(30.dp)
        ) {
            ScriptTab.values().forEach { scriptTab ->
                val enabled = scriptTab.isEnabled()
                Tab(
                    text = { Text(scriptTab.title) },
                    modifier = Modifier.handPointerIcon(),
                    selected = currentPage == scriptTab,
                    unselectedContentColor = if (enabled) Color.LightGray else Color.Black,
                    onClick = {
                        ScriptTabsUIUtil.updateCurrentTab(scriptTab)
                        scriptTab.onTabSelect()
                    },
                    enabled = enabled,
                )
            }
        }
        ScriptTabContent()
    }
}