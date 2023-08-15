package fr.lewon.dofus.bot.scripts.tasks.impl.inventory

import fr.lewon.dofus.bot.core.d2o.managers.item.ItemManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.sets.CharacterSetElement
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import java.awt.event.KeyEvent

class UseItemInItemBarTask(private val characterSetElement: CharacterSetElement) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))
        val keyEvent = KeyEvent.getExtendedKeyCodeForChar(characterSetElement.key.code)
        KeyboardUtil.sendKey(gameInfo, keyEvent, 300, characterSetElement.ctrlModifier)
        return true
    }

    override fun onStarted(): String {
        val item = characterSetElement.elementId?.toDouble()?.let(ItemManager::getItem)
        return "Using item in item bar : ${item?.name} ..."
    }
}