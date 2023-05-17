package fr.lewon.dofus.bot.gui.custom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.util.AppColors


@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    text: String,
    iconPainter: Painter,
    modifier: Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = AppColors.backgroundColor,
    textColor: Color = Color.White
) {
    AnimatedButton(onClick, text, modifier, shape, backgroundColor, textColor) {
        Image(iconPainter, "", Modifier.fillMaxSize())
    }
}

@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    text: String,
    imageVector: ImageVector,
    modifier: Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = AppColors.backgroundColor,
    textColor: Color = Color.White,
    iconColor: Color = AppColors.primaryColor
) {
    AnimatedButton(onClick, text, modifier, shape, backgroundColor, textColor) {
        Image(imageVector, "", Modifier.fillMaxSize(), colorFilter = ColorFilter.tint(iconColor))
    }
}

@Composable
private fun AnimatedButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier,
    shape: Shape,
    backgroundColor: Color,
    textColor: Color,
    imageContent: @Composable () -> Unit
) {
    val isHovered = remember { mutableStateOf(false) }
    Button(
        onClick = onClick,
        shape = shape,
        modifier = modifier.defaultHoverManager(isHovered).handPointerIcon(),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        contentPadding = PaddingValues(0.dp)
    ) {
        AnimatedVisibility(
            visible = isHovered.value,
            enter = expandHorizontally(expandFrom = Alignment.End),
            exit = shrinkHorizontally(shrinkTowards = Alignment.End)
        ) {
            Text(
                text,
                Modifier.fillMaxWidth().align(Alignment.CenterVertically),
                textAlign = TextAlign.Center,
                color = textColor
            )
        }
        AnimatedVisibility(
            visible = !isHovered.value,
            enter = expandHorizontally(expandFrom = Alignment.Start),
            exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
        ) {
            imageContent()
        }
    }
}