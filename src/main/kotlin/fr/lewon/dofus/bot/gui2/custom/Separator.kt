package fr.lewon.dofus.bot.gui2.custom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalSeparator() {
    Divider(Modifier.fillMaxWidth().padding(horizontal = 8.dp).height(1.dp))
}