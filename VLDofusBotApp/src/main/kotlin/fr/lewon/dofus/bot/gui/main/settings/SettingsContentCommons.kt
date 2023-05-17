package fr.lewon.dofus.bot.gui.main.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.SoundType

@Composable
fun TestSoundButton(soundType: SoundType, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Row(modifier.padding(end = 10.dp).height(30.dp)) {
        ButtonWithTooltip(
            { soundType.playSound(true) },
            "",
            imageVector = Icons.Default.PlayArrow,
            RoundedCornerShape(15),
            Color.Gray,
            AppColors.VERY_DARK_BG_COLOR,
            enabled = enabled
        )
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
        Box(Modifier.onTabChangeFocus(LocalFocusManager.current).onFocusHighlight()) {
            content()
        }
    }
}