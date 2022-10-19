package fr.lewon.dofus.bot.gui2.custom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.gui2.util.AppColors

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleTextField(
    value: String,
    onValueChange: (value: String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isContentValid: (value: String) -> Boolean = { true }
) {
    val textFieldValueState = remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.value.copy(text = value)
    val previousText = mutableStateOf(textFieldValue.text)
    BasicTextField(
        value = textFieldValue,
        enabled = enabled,
        onValueChange = { newValue ->
            if (isContentValid(newValue.text)) {
                onValueChange(newValue.text)
                textFieldValueState.value = newValue
                previousText.value = newValue.text
            } else {
                val backupTfv = TextFieldValue(previousText.value, TextRange(textFieldValue.selection.start))
                textFieldValueState.value = backupTfv
                onValueChange("INVALID_${newValue.text}")
                onValueChange(previousText.value)
            }
        },
        modifier = modifier.onPreviewKeyEvent {
            if (it.key == Key.A && it.type == KeyEventType.KeyDown && it.isCtrlPressed) {
                val tfv = textFieldValueState.value
                textFieldValueState.value = tfv.copy(selection = TextRange(0, tfv.text.length))
                true
            } else {
                false
            }
        }.onFocusSelectAll(textFieldValueState),
        cursorBrush = SolidColor(Color.White),
        textStyle = TextStyle(fontSize = 13.sp, color = Color.White),
        decorationBox = { innerTextField ->
            Row(
                Modifier
                    .background(AppColors.backgroundColor, RoundedCornerShape(5.dp))
                    .padding(3.dp)
                    .border(BorderStroke(1.dp, AppColors.backgroundColor)).padding(horizontal = 5.dp)
            ) {
                Row(Modifier.align(Alignment.CenterVertically)) {
                    innerTextField()
                }
            }
        }
    )
}

private fun Modifier.onFocusSelectAll(textFieldValueState: MutableState<TextFieldValue>): Modifier =
    composed(
        inspectorInfo = debugInspectorInfo {
            name = "textFieldValueState"
            properties["textFieldValueState"] = textFieldValueState
        }
    ) {
        var triggerEffect by remember {
            mutableStateOf<Boolean?>(null)
        }
        if (triggerEffect != null) {
            LaunchedEffect(triggerEffect) {
                val tfv = textFieldValueState.value
                textFieldValueState.value = tfv.copy(selection = TextRange(0, tfv.text.length))
            }
        }
        Modifier.onFocusChanged { focusState ->
            if (focusState.isFocused) {
                triggerEffect = triggerEffect?.let { bool ->
                    !bool
                } ?: true
            }
        }
    }