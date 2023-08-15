package fr.lewon.dofus.bot.gui.custom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.gui.util.AppColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleTextField(
    text: String,
    onValueChange: (value: String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isContentValid: (value: String) -> Boolean = { true },
    backgroundColor: Color = AppColors.VERY_DARK_BG_COLOR,
    borderColor: Color = backgroundColor,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    inputHandlers: List<KeyHandler> = emptyList(),
    placeHolderText: String = "",
    placeHolderColor: Color = getPlaceHolderColor(backgroundColor),
) {
    val textFieldValueState = remember {
        mutableStateOf(
            TextFieldValue(
                text = text,
                composition = TextRange(0, 0)
            )
        )
    }
    LaunchedEffect(text) {
        if (textFieldValueState.value.text != text) {
            textFieldValueState.value = textFieldValueState.value.copy(text = text)
        }
    }
    val previousText = remember { mutableStateOf(textFieldValueState.value.text) }
    val textSelectionColors = TextSelectionColors(
        handleColor = Color.White,
        backgroundColor = Color.Gray,
    )
    val coroutineScope = rememberCoroutineScope()
    CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
        BasicTextField(
            value = textFieldValueState.value,
            enabled = enabled,
            onValueChange = { newValue ->
                val newText = newValue.text.replace("\n", "")
                if (isContentValid(newText)) {
                    onValueChange(newText)
                }
                textFieldValueState.value = newValue
                previousText.value = newText
            },
            maxLines = 1,
            modifier = modifier.onFocusChanged {
                val hasFocus = it.isFocused || it.hasFocus
                if (!hasFocus) {
                    onValueChange(textFieldValueState.value.text)
                }
                coroutineScope.launch {
                    textFieldValueState.value = textFieldValueState.value.copy(
                        selection = if (hasFocus) {
                            TextRange(0, textFieldValueState.value.text.length)
                        } else TextRange(0, 0),
                        composition = if (hasFocus) {
                            textFieldValueState.value.composition
                        } else TextRange(0, 0),
                        text = if (!isContentValid(textFieldValueState.value.text)) {
                            text
                        } else textFieldValueState.value.text
                    )
                }
            }.onPreviewKeyEvent { keyEvent ->
                keyEvent.isCtrlPressed && keyEvent.key == Key.Backspace && textFieldValueState.value.text.isEmpty()
                    || keyEvent.type == KeyEventType.KeyDown
                    && inputHandlers.filter { it.checkKey(keyEvent) }.onEach { it.handleKeyEvent() }
                    .isNotEmpty()
            },
            cursorBrush = SolidColor(Color.White),
            textStyle = TextStyle(fontSize = 13.sp, color = Color.White),
            singleLine = false,
            decorationBox = { innerTextField ->
                Row(
                    Modifier.background(backgroundColor, RoundedCornerShape(5.dp))
                        .height(25.dp)
                        .padding(3.dp)
                        .border(BorderStroke(1.dp, borderColor)).padding(horizontal = 5.dp)
                ) {
                    leadingIcon?.let {
                        Image(it, "", Modifier.height(23.dp).align(Alignment.CenterVertically).padding(end = 5.dp))
                    }
                    Box(Modifier.align(Alignment.CenterVertically).fillMaxWidth().weight(1f)) {
                        if (textFieldValueState.value.text.isEmpty()) {
                            CommonText(
                                placeHolderText,
                                enabledColor = placeHolderColor,
                                fontSize = 12.sp,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                        }
                        innerTextField()
                    }
                    if (!isContentValid(textFieldValueState.value.text)) {
                        TooltipTarget(
                            "Invalid content",
                            modifier = Modifier.height(23.dp).align(Alignment.CenterVertically)
                        ) {
                            Image(
                                Icons.Default.Warning,
                                "",
                                colorFilter = ColorFilter.tint(Color.Red)
                            )
                        }
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

private fun getPlaceHolderColor(backgroundColor: Color): Color {
    val getColorComponent: (Float) -> Float = {
        if (it < 0.5f) it + 0.2f else it - 0.2f
    }
    return backgroundColor.copy(
        red = getColorComponent(backgroundColor.red),
        green = getColorComponent(backgroundColor.green),
        blue = getColorComponent(backgroundColor.blue)
    )
}