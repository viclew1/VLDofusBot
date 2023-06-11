package fr.lewon.dofus.bot.gui.custom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.gui.util.AppColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleTextField(
    value: String,
    onValueChange: (value: String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isContentValid: (value: String) -> Boolean = { true },
    backgroundColor: Color = AppColors.VERY_DARK_BG_COLOR,
    borderColor: Color = backgroundColor,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    inputHandlers: List<KeyHandler> = emptyList()
) {
    val textFieldValueState = remember { mutableStateOf(TextFieldValue(text = value, composition = TextRange(0, 0))) }
    val textFieldValue = textFieldValueState.value.copy(text = value)
    val previousText = mutableStateOf(textFieldValue.text)
    val textSelectionColors = TextSelectionColors(
        handleColor = Color.White,
        backgroundColor = Color.Gray,
    )
    CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
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
            maxLines = 1,
            modifier = modifier.onFocusSelectAll(textFieldValueState).onFocusLeaveResetSelection(textFieldValueState)
                .onPreviewKeyEvent { keyEvent ->
                    keyEvent.isCtrlPressed && keyEvent.key == Key.Backspace && textFieldValue.text.isEmpty()
                            || keyEvent.type == KeyEventType.KeyDown
                            && inputHandlers.filter { it.checkKey(keyEvent) }.onEach { it.handleKeyEvent() }
                        .isNotEmpty()
                },
            cursorBrush = SolidColor(Color.White),
            textStyle = TextStyle(fontSize = 13.sp, color = Color.White),
            decorationBox = { innerTextField ->
                Row(
                    Modifier.background(backgroundColor, RoundedCornerShape(5.dp))
                        .padding(3.dp)
                        .border(BorderStroke(1.dp, borderColor)).padding(horizontal = 5.dp)
                ) {
                    leadingIcon?.let {
                        Image(it, "", Modifier.height(23.dp).align(Alignment.CenterVertically).padding(end = 5.dp))
                    }
                    Row(Modifier.align(Alignment.CenterVertically).fillMaxWidth().weight(1f)) {
                        innerTextField()
                    }
                    trailingIcon?.let {
                        Image(
                            it,
                            "",
                            Modifier.height(23.dp).align(Alignment.CenterVertically),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }
                }
            },
        )
    }
}

@Composable
private fun Modifier.onFocusLeaveResetSelection(
    textFieldValueState: MutableState<TextFieldValue>,
    scope: CoroutineScope = rememberCoroutineScope()
): Modifier = onFocusChanged {
    if (!it.isFocused && !it.hasFocus) {
        scope.launch {
            delay(20)
            textFieldValueState.value = textFieldValueState.value.copy(
                selection = TextRange(0, 0),
                composition = TextRange(0, 0)
            )
        }
    }
}

@Composable
private fun Modifier.onFocusSelectAll(
    textFieldValueState: MutableState<TextFieldValue>,
    scope: CoroutineScope = rememberCoroutineScope()
): Modifier = onFocusChanged {
    if (it.isFocused || it.hasFocus) {
        scope.launch {
            delay(20)
            val text = textFieldValueState.value.text
            textFieldValueState.value = textFieldValueState.value.copy(
                selection = TextRange(0, text.length),
            )
        }
    }
}