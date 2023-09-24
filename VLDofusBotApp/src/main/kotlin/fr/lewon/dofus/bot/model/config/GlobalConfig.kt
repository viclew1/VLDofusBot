package fr.lewon.dofus.bot.model.config

import com.fasterxml.jackson.annotation.JsonProperty
import fr.lewon.dofus.bot.scripts.tasks.impl.harvest.TransferItemsToBankBehaviour
import fr.lewon.dofus.bot.util.listeners.OverlayInfo
import java.util.*

data class GlobalConfig(
    @field:JsonProperty var displayOverlays: Boolean = true,
    @field:JsonProperty var shouldDisplayOverlay: EnumMap<OverlayInfo, Boolean> = EnumMap(OverlayInfo::class.java),
    @field:JsonProperty var networkInterfaceName: String = "",
    @field:JsonProperty var enableSounds: Boolean = true,
    @field:JsonProperty var playArchMonsterSound: Boolean = true,
    @field:JsonProperty var playQuestMonsterSound: Boolean = true,
    @field:JsonProperty var stopAnyScriptOnArchmonsterFound: Boolean = false,
    @field:JsonProperty var transferItemsToBankBehaviour: TransferItemsToBankBehaviour = TransferItemsToBankBehaviour.TransferAllVisibleObjects,
) {

    fun deepCopy(): GlobalConfig {
        return GlobalConfig(
            displayOverlays,
            EnumMap(shouldDisplayOverlay),
            networkInterfaceName,
            enableSounds,
            playArchMonsterSound,
            playQuestMonsterSound,
            stopAnyScriptOnArchmonsterFound,
            transferItemsToBankBehaviour
        )
    }

    fun shouldDisplayOverlay(toToggleOverlay: OverlayInfo): Boolean {
        return shouldDisplayOverlay[toToggleOverlay] ?: toToggleOverlay.defaultDisplay
    }
}