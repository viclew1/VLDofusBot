package fr.lewon.dofus.bot.gui.main.settings.global

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import fr.lewon.dofus.bot.gui.main.settings.ConfigSwitchLine
import fr.lewon.dofus.bot.gui.main.settings.SettingsUIUtil
import fr.lewon.dofus.bot.gui.main.settings.TestSoundButton
import fr.lewon.dofus.bot.gui.util.SoundType

@Composable
fun NotificationSoundsParametersContent() {
    val globalConfig = SettingsUIUtil.SETTINGS_UI_STATE.value.globalConfig
    Column {
        ConfigSwitchLine(
            "Notification sounds",
            "",
            true,
            globalConfig.enableSounds
        ) { checked ->
            SettingsUIUtil.updateGlobalConfig { it.enableSounds = checked }
        }
        Row {
            TestSoundButton(SoundType.ARCH_MONSTER_FOUND, Modifier.align(Alignment.CenterVertically))
            ConfigSwitchLine(
                "Archmonster sounds",
                "Plays a sound when an archmonster is on an initialized character's map",
                globalConfig.enableSounds,
                globalConfig.playArchMonsterSound
            ) { checked -> SettingsUIUtil.updateGlobalConfig { it.playArchMonsterSound = checked } }
        }
        Row {
            TestSoundButton(SoundType.QUEST_MONSTER_FOUND, Modifier.align(Alignment.CenterVertically))
            ConfigSwitchLine(
                "Quest monster sounds",
                "Plays a sound when a quest monster is on an initialized character's map",
                globalConfig.enableSounds,
                globalConfig.playQuestMonsterSound
            ) { checked -> SettingsUIUtil.updateGlobalConfig { it.playQuestMonsterSound = checked } }
        }
    }
}