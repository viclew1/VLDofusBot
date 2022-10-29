package fr.lewon.dofus.bot.gui2.custom

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.gui2.util.AppColors

@Composable
fun TitleText(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit = 18.sp,
    fontWeight: FontWeight? = FontWeight.Bold
) {
    CustomCompositionLocalProvider {
        Text(text, modifier, if (enabled) Color.White else Color.Gray, fontSize, fontWeight = fontWeight)
    }
}

@Composable
fun SubTitleText(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight? = null,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
    enabledColor: Color = Color.White,
    disabledColor: Color = Color.Gray,
    textAlign: TextAlign? = null
) {
    CustomCompositionLocalProvider {
        Text(
            text,
            modifier,
            if (enabled) enabledColor else disabledColor,
            fontSize,
            fontWeight = fontWeight,
            overflow = overflow,
            maxLines = maxLines,
            textAlign = textAlign
        )
    }
}

@Composable
fun CommonText(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit = 13.sp,
    fontWeight: FontWeight? = null,
    enabledColor: Color = Color.White,
    disabledColor: Color = Color.Gray,
    textAlign: TextAlign? = null
) {
    CustomCompositionLocalProvider {
        Text(
            text,
            modifier,
            if (enabled) enabledColor else disabledColor,
            fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign
        )
    }
}

@Composable
fun SmallText(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit = 11.sp,
    fontWeight: FontWeight? = null
) {
    CustomCompositionLocalProvider {
        Text(text, modifier, if (enabled) Color.LightGray else Color.DarkGray, fontSize, fontWeight = fontWeight)
    }
}

@Composable
fun CustomCompositionLocalProvider(content: @Composable () -> Unit) = CompositionLocalProvider(
    LocalTextSelectionColors provides TextSelectionColors(
        backgroundColor = Color.Gray,
        handleColor = AppColors.primaryDarkColor
    ),
) {
    content()
}