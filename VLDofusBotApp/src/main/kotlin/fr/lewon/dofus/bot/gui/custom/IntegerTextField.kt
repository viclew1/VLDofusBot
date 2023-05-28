package fr.lewon.dofus.bot.gui.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged

@Composable
fun IntegerTextField(value: String, onUpdate: (String) -> Unit, modifier: Modifier = Modifier) {
    SimpleTextField(value, onUpdate, isContentValid = { newValue ->
        newValue.isEmpty() || newValue == "-" || newValue.toIntOrNull() != null
    }, modifier = modifier.onFocusChanged {
        if (!it.isFocused && value.toIntOrNull() == null) {
            onUpdate("0")
        }
    })
}