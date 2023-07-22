package fr.lewon.dofus.bot.gui.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun IntegerTextField(value: String, onUpdate: (String) -> Unit, modifier: Modifier = Modifier) =
    NumberTextField(value, { it.toIntOrNull() }, onUpdate, modifier)

@Composable
fun LongTextField(value: String, onUpdate: (String) -> Unit, modifier: Modifier = Modifier) =
    NumberTextField(value, { it.toLongOrNull() }, onUpdate, modifier)

@Composable
private fun <T : Number?> NumberTextField(
    value: String,
    parseValue: (String) -> T,
    onUpdate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    SimpleTextField(
        text = value,
        onValueChange = {
            onUpdate(it)
        },
        isContentValid = { parseValue(it) != null },
        modifier = modifier
    )
}