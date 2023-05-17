package fr.lewon.dofus.bot.gui.custom

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HorizontalSeparator(title: String = "", modifier: Modifier = Modifier) {
    if (title.isBlank()) {
        Divider(modifier.fillMaxWidth().padding(horizontal = 8.dp).height(1.dp))
    } else {
        Row(modifier.fillMaxWidth()) {
            Divider(
                Modifier.align(Alignment.CenterVertically).width(30.dp).padding(start = 8.dp, end = 5.dp).height(1.dp)
            )
            CommonText(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Divider(
                Modifier.align(Alignment.CenterVertically).fillMaxWidth().padding(start = 5.dp, end = 8.dp).height(1.dp)
            )
        }
    }
}