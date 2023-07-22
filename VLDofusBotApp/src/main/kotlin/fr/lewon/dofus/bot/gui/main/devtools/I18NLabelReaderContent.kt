package fr.lewon.dofus.bot.gui.main.devtools

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.gui.custom.*

@Composable
fun I18NLabelReaderContent() {
    val labelId = remember { mutableStateOf(0) }
    val label = remember { mutableStateOf("/") }
    Column(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle().padding(5.dp)) {
        Row {
            CommonText(
                "I18N Label Reader",
                modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(Modifier.height(30.dp)) {
            CommonText(
                "Label ID : ",
                modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
            )
            IntegerTextField(
                value = labelId.value.toString(),
                onUpdate = { labelId.value = it.toIntOrNull() ?: 0 },
                modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically).fillMaxWidth().weight(1f),
            )
            ButtonWithTooltip(
                onClick = { label.value = I18NUtil.getLabel(labelId.value) ?: "[NO LABEL FOUND]" },
                title = "Search label",
                shape = RectangleShape,
                imageVector = Icons.Default.Search,
                imageModifier = Modifier.size(25.dp)
            )
        }
        HorizontalSeparator(modifier = Modifier.padding(5.dp))
        CommonText(label.value, modifier = Modifier.padding(4.dp))
    }
}