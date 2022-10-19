package fr.lewon.dofus.bot.gui2.main.scripts.characters

import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIUtil
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.util.script.ScriptRunner
import java.awt.image.BufferedImage

data class CharacterUIState(
    val name: String,
    val dofusClassId: Int,
    val activityState: CharacterActivityState = CharacterActivityState.DISCONNECTED,
    val checked: Boolean = false,
    val runningScript: ScriptRunner.RunningScript? = null,
    val scriptBuilder: DofusBotScriptBuilder = ScriptTabsUIUtil.scripts.first(),
    val entityLook: EntityLook = EntityLook(),
    val skinImage: BufferedImage? = null
)