package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.EffectManager
import fr.lewon.dofus.bot.core.manager.ItemManager
import fr.lewon.dofus.bot.gui.alert.SoundType
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.smithmagic.SmithMagicCharacteristics
import fr.lewon.dofus.bot.scripts.smithmagic.SmithMagicLine
import fr.lewon.dofus.bot.scripts.smithmagic.SmithMagicStrategy
import fr.lewon.dofus.bot.scripts.smithmagic.SmithMagicType
import fr.lewon.dofus.bot.scripts.smithmagic.strategies.DrakeHeadStrategy
import fr.lewon.dofus.bot.sniffer.model.messages.exchange.ExchangeObjectAddedMessage
import fr.lewon.dofus.bot.sniffer.model.messages.exchange.ExchangeStartOkCraftWithInformationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.smithmagic.ObjectModifiedMessage
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.ObjectItem
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.effect.ObjectEffectInteger
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class SmithMagicScript : DofusBotScript("Smith magic") {

    companion object {
        private val STRATEGIES = mapOf(
            "Drake head" to DrakeHeadStrategy()
        )

        private val FIRST_LINE_BASIC_RUNE_POSITION = PointRelative(0.6189781f, 0.31934306f)
        private val FIRST_LINE_BIGGEST_RUNE_POSITION = PointRelative(0.69635034f, 0.3138686f)
        private val TENTH_LINE_BASIC_RUNE_POSITION = PointRelative(0.6189781f, 0.6587591f)
        private val LINE_DELTA_X = FIRST_LINE_BIGGEST_RUNE_POSITION.getDifference(FIRST_LINE_BASIC_RUNE_POSITION).x / 2f
        private val LINE_DELTA_Y = TENTH_LINE_BASIC_RUNE_POSITION.getDifference(FIRST_LINE_BASIC_RUNE_POSITION).y / 9f

        private val CLEAR_SEARCH_POSITION = PointRelative(0.99270076f, 0.7591241f)
        private val SEARCH_POSITION = PointRelative(0.83795613f, 0.76459855f)
        private val FIRST_ITEM_POSITION = PointRelative(0.7781022f, 0.17883211f)
        private val MERGE_BUTTON_POSITION = PointRelative(0.56642336f, 0.21715327f)
    }

    private val strategyParameter = DofusBotParameter(
        "strategy",
        "Smithing Strategy used to improve the item",
        STRATEGIES.keys.first(),
        DofusBotParameterType.CHOICE,
        STRATEGIES.keys.toList()
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(
            strategyParameter
        )
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Executes a smithing strategy on an item you have to chose manually once the smithing interface is opened"
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val strategy = STRATEGIES[strategyParameter.value] ?: error("No strategy selected")
        openNeededWorkshop(gameInfo, strategy.getSmithMagicType())
        gameInfo.logger.addSubLog("Workshop opened.", logItem)
        while (true) {
            gameInfo.logger.addSubLog("Waiting for an item to be selected ...", logItem)
            WaitUtil.waitUntilMessageArrives(gameInfo, ExchangeObjectAddedMessage::class.java)
            val currentObjectItem = gameInfo.eventStore.getLastEvent(ExchangeObjectAddedMessage::class.java)?.objectItem
                ?: error("Missing message in store")
            val smithMagicLogItem = gameInfo.logger.addSubLog("Item selected ! Starting smithing ...", logItem)
            applyStrategyOnItem(gameInfo, currentObjectItem, strategy)
            gameInfo.logger.closeLog("OK", smithMagicLogItem, true)
            SoundType.OBJECT_CRAFT.playSound()
        }
    }

    private fun applyStrategyOnItem(gameInfo: GameInfo, objectItem: ObjectItem, strategy: SmithMagicStrategy) {
        val linesByKeyword = buildSmithMagicLines(objectItem)
            .associateBy { it.characteristicKeyWord }
            .toMutableMap()
        var currentObjectItem = objectItem
        updateSmithMagicLinesValues(currentObjectItem, linesByKeyword)
        var previousSearchedCharacteristic: SmithMagicCharacteristics? = null
        while (!strategy.checkEnd(linesByKeyword)) {
            val runeToPass = strategy.getRuneToPass(linesByKeyword)
            val correspondingLine = linesByKeyword[runeToPass.second.keyWord]
            gameInfo.eventStore.clear()
            if (correspondingLine != null) {
                useRuneOnLine(gameInfo, linesByKeyword, correspondingLine, runeToPass.first)
            } else {
                useRuneInInventory(gameInfo, runeToPass.second, runeToPass.first, previousSearchedCharacteristic)
                previousSearchedCharacteristic = runeToPass.second
            }
            currentObjectItem = waitForObjectModified(gameInfo) ?: currentObjectItem
            updateSmithMagicLinesValues(currentObjectItem, linesByKeyword)
        }
    }

    private fun useRuneInInventory(
        gameInfo: GameInfo,
        characteristic: SmithMagicCharacteristics,
        runeSize: Int,
        previousSearchedCharacteristic: SmithMagicCharacteristics? = null
    ) {
        if (characteristic != previousSearchedCharacteristic) {
            MouseUtil.leftClick(gameInfo, CLEAR_SEARCH_POSITION)
            MouseUtil.leftClick(gameInfo, SEARCH_POSITION, 500)
            KeyboardUtil.writeKeyboard(gameInfo, "Rune ga pa", 500)
        }
        MouseUtil.doubleLeftClick(gameInfo, FIRST_ITEM_POSITION, 1000)
        MouseUtil.leftClick(gameInfo, MERGE_BUTTON_POSITION)
        WaitUtil.sleep(1000)
    }

    private fun useRuneOnLine(
        gameInfo: GameInfo,
        linesByKeyword: MutableMap<String, SmithMagicLine>,
        correspondingLine: SmithMagicLine,
        runeSize: Int
    ) {
        val lineIndex = getLineIndex(linesByKeyword.values, correspondingLine)
        val runeClickPosition = getRuneClickPosition(lineIndex, runeSize)
        MouseUtil.leftClick(gameInfo, runeClickPosition)
    }

    private fun waitForObjectModified(gameInfo: GameInfo): ObjectItem? {
        WaitUtil.waitUntil({ gameInfo.eventStore.getLastEvent(ObjectModifiedMessage::class.java) != null }, 10000)
        gameInfo.eventStore.clearUntilLast(ObjectModifiedMessage::class.java)
        return gameInfo.eventStore.getLastEvent(ObjectModifiedMessage::class.java)?.objectItem
    }

    private fun getRuneClickPosition(lineIndex: Int, runeSize: Int): PointRelative {
        return PointRelative(
            FIRST_LINE_BASIC_RUNE_POSITION.x + LINE_DELTA_X * runeSize,
            FIRST_LINE_BASIC_RUNE_POSITION.y + LINE_DELTA_Y * lineIndex
        )
    }

    private fun getLineIndex(lines: Collection<SmithMagicLine>, line: SmithMagicLine): Int {
        return lines.sortedBy { it.order }.indexOf(line)
    }

    private fun buildSmithMagicLines(item: ObjectItem): List<SmithMagicLine> {
        val genericItem = ItemManager.getItem(item.objectGID.toDouble())
        val lines = ArrayList<SmithMagicLine>()
        for (effect in genericItem.effects) {
            val order = effect.characteristic.categoryId * Short.MAX_VALUE.toInt() + effect.characteristic.order
            lines.add(SmithMagicLine(effect.min, effect.max, effect.characteristic.keyWord, order))
        }
        return lines
    }

    private fun updateSmithMagicLinesValues(item: ObjectItem, linesByKeyword: MutableMap<String, SmithMagicLine>) {
        linesByKeyword.values.forEach { it.current = 0 }
        for (effect in item.effects) {
            if (effect is ObjectEffectInteger) {
                val characteristic = EffectManager.getCharacteristicByEffectId(effect.actionId.toDouble())
                if (characteristic != null) {
                    val line = linesByKeyword.computeIfAbsent(characteristic.keyWord) {
                        SmithMagicLine(0, 1000, it)
                    }
                    line.current = effect.value
                }
            }
        }
    }

    private fun openNeededWorkshop(gameInfo: GameInfo, smithMagicType: SmithMagicType) {
        val skillId = smithMagicType.skillId
        val element = gameInfo.interactiveElements.firstOrNull {
            it.enabledSkills.map { enabledSkill -> enabledSkill.skillId }.contains(skillId)
        } ?: error("Couldn't find a workshop for type : $smithMagicType")
        val clickPosition = InteractiveUtil.getElementClickPosition(gameInfo, element.elementId)
        MouseUtil.leftClick(gameInfo, clickPosition)
        WaitUtil.waitUntilMessageArrives(gameInfo, ExchangeStartOkCraftWithInformationMessage::class.java)
    }

}