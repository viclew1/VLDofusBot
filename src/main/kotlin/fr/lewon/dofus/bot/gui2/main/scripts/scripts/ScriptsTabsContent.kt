package fr.lewon.dofus.bot.gui2.main.scripts.scripts

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
import fr.lewon.dofus.bot.gui2.custom.handPointerIcon
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.ScriptTabContent
import fr.lewon.dofus.bot.gui2.util.AppColors

@Composable
fun ScriptsTabsContent() {
    val currentPage = ScriptTabsUIUtil.currentPage.value
    Column {
        TabRow(
            selectedTabIndex = currentPage.ordinal,
            backgroundColor = MaterialTheme.colors.background,
            contentColor = AppColors.primaryLightColor,
            modifier = Modifier.height(30.dp)
        ) {
            ScriptTab.values().forEach { scriptTab ->
                Tab(
                    text = { Text(scriptTab.title) },
                    modifier = Modifier.handPointerIcon(),
                    selected = currentPage == scriptTab,
                    unselectedContentColor = Color.LightGray,
                    onClick = {
                        ScriptTabsUIUtil.currentPage.value = scriptTab
                        scriptTab.onTabSelect()
                    },
                    enabled = scriptTab.isEnabled()
                )
            }
        }
        ScriptTabContent()
    }
}