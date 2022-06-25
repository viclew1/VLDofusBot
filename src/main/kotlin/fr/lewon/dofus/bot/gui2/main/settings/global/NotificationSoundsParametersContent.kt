package fr.lewon.dofus.bot.gui2.main.settings.global

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import fr.lewon.dofus.bot.gui2.main.settings.ConfigSwitchLine
import fr.lewon.dofus.bot.gui2.main.settings.SettingsUIState
import fr.lewon.dofus.bot.gui2.main.settings.TestSoundButton
import fr.lewon.dofus.bot.gui2.util.SoundType

@Composable
fun NotificationSoundsParametersContent() {
    Column {
        ConfigSwitchLine(
            "Notification sounds",
            "",
            true,
            SettingsUIState.settingsGlobalConfig.value.enableSounds
        ) { checked ->
            SettingsUIState.updateGlobalConfig { it.enableSounds = checked }
        }
        Row {
            TestSoundButton(SoundType.ARCH_MONSTER_FOUND, Modifier.align(Alignment.CenterVertically))
            ConfigSwitchLine(
                "Archmonster sounds",
                "Plays a sound when an archmonster is on an initialized character's map",
                SettingsUIState.settingsGlobalConfig.value.enableSounds,
                SettingsUIState.settingsGlobalConfig.value.playArchMonsterSound
            ) { checked -> SettingsUIState.updateGlobalConfig { it.playArchMonsterSound = checked } }
        }
        Row {
            TestSoundButton(SoundType.QUEST_MONSTER_FOUND, Modifier.align(Alignment.CenterVertically))
            ConfigSwitchLine(
                "Quest monster sounds",
                "Plays a sound when a quest monster is on an initialized character's map",
                SettingsUIState.settingsGlobalConfig.value.enableSounds,
                SettingsUIState.settingsGlobalConfig.value.playQuestMonsterSound
            ) { checked -> SettingsUIState.updateGlobalConfig { it.playQuestMonsterSound = checked } }
        }
    }
}