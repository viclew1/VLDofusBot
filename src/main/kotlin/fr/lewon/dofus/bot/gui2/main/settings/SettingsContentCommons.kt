package fr.lewon.dofus.bot.gui2.main.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.*
import fr.lewon.dofus.bot.gui2.util.SoundType

@Composable
fun TestSoundButton(soundType: SoundType, modifier: Modifier = Modifier, enabled: Boolean = true) {
    IconButton({ soundType.playSound(true) }, Icons.Default.PlayArrow, modifier, enabled)
}

@Composable
fun IconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconSize: Dp = 32.dp,
    shape: Shape = RoundedCornerShape(15)
) {
    Row(modifier.padding(end = 10.dp)) {
        val backgroundColor = if (enabled) Color.White else Color.DarkGray

        OutlinedButton(
            onClick,
            border = BorderStroke(1.dp, backgroundColor),
            shape = shape,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(iconSize, iconSize)
        ) {
            Image(
                icon,
                "",
                Modifier.size(iconSize, iconSize).handPointerIcon(),
                colorFilter = ColorFilter.tint(backgroundColor)
            )
        }
    }
}

@Composable
fun ConfigTextField(
    value: String,
    onValueChange: (newValue: String) -> Unit,
    modifier: Modifier = Modifier.width(260.dp),
    enabled: Boolean = true
) {
    SimpleTextField(value, onValueChange, modifier, enabled)
}

@Composable
fun ConfigSwitchLine(
    title: String,
    description: String,
    enabled: Boolean,
    value: Boolean,
    onCheck: (checked: Boolean) -> Unit
) {
    ConfigLine(title, description, enabled) {
        Switch(value, onCheck, enabled = enabled)
    }
}

@Composable
fun ConfigLine(title: String, description: String, enabled: Boolean, content: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(50.dp), verticalAlignment = Alignment.CenterVertically) {
        Column {
            if (description.isEmpty()) {
                SubTitleText(title, enabled = enabled)
            } else {
                CommonText(title, enabled = enabled)
                SmallText(description, enabled = enabled)
            }
        }
        Spacer(Modifier.weight(1f))
        content()
    }
}