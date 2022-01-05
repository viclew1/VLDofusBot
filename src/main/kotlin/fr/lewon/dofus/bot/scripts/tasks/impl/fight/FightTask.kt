package fr.lewon.dofus.bot.scripts.tasks.impl.fight

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.DofusUIPositionsManager
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.AIComplement
import fr.lewon.dofus.bot.game.fight.FightAI
import fr.lewon.dofus.bot.game.fight.FighterCharacteristic
import fr.lewon.dofus.bot.game.fight.complements.DefaultAIComplement
import fr.lewon.dofus.bot.game.fight.operations.FightOperation
import fr.lewon.dofus.bot.game.fight.operations.FightOperationType
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.sniffer.model.messages.fight.*
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.SetCharacterRestrictionsMessage
import fr.lewon.dofus.bot.util.game.DefaultUIPositions
import fr.lewon.dofus.bot.util.game.DofusColors
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.game.RetryUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.*
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.event.KeyEvent

class FightTask(
    private val aiComplement: AIComplement = DefaultAIComplement(),
    private val teamFight: Boolean = false
) : BooleanDofusBotTask() {

    companion object {

        private val LVL_UP_OK_BUTTON_POINT = PointRelative(0.47435898f, 0.6868327f)
        private val REF_TOP_LEFT_POINT = PointRelative(0.4016129f, 0.88508064f)

        private val REF_CREATURE_MODE_BUTTON_BOUNDS = RectangleRelative.build(
            PointRelative(0.8946648f, 0.96410257f),
            PointRelative(0.90834475f, 0.982906f)
        )

        private val REF_BLOCK_HELP_BUTTON_BOUNDS = RectangleRelative.build(
            PointRelative(0.90697676f, 0.8666667f),
            PointRelative(0.9138167f, 0.88376063f)
        )

        private val REF_RESTRICT_TO_TEAM_BUTTON_BOUNDS = RectangleRelative.build(
            PointRelative(0.94516134f, 0.86693543f),
            PointRelative(0.96129036f, 0.88306457f)
        )

        private val CLOSE_FIGHT_BUTTON_1 = RectangleRelative.build(
            PointRelative(0.9419354f, 0.27620968f),
            PointRelative(0.95645154f, 0.30040324f)
        )

        private val CLOSE_FIGHT_BUTTON_2 = RectangleRelative.build(
            PointRelative(0.716129f, 0.69153225f),
            PointRelative(0.7306452f, 0.7076613f)
        )

        private val MIN_COLOR = DofusColors.HIGHLIGHT_COLOR_MIN
        private val MAX_COLOR = DofusColors.HIGHLIGHT_COLOR_MAX
        private val MIN_COLOR_CROSS = DofusColors.UI_BANNER_BLACK_COLOR_MIN
        private val MAX_COLOR_CROSS = DofusColors.UI_BANNER_BLACK_COLOR_MAX
        private val MIN_COLOR_BG = DofusColors.UI_BANNER_GREY_COLOR_MIN
        private val MAX_COLOR_BG = DofusColors.UI_BANNER_GREY_COLOR_MAX
    }

    private fun getCloseButtonLocation(gameInfo: GameInfo): RectangleRelative? {
        if (ScreenUtil.colorCount(gameInfo, CLOSE_FIGHT_BUTTON_1, MIN_COLOR_CROSS, MAX_COLOR_CROSS) > 0
            && ScreenUtil.colorCount(gameInfo, CLOSE_FIGHT_BUTTON_1, MIN_COLOR_BG, MAX_COLOR_BG) > 0
        ) {
            return CLOSE_FIGHT_BUTTON_1
        }
        if (ScreenUtil.colorCount(gameInfo, CLOSE_FIGHT_BUTTON_2, MIN_COLOR_CROSS, MAX_COLOR_CROSS) > 0
            && ScreenUtil.colorCount(gameInfo, CLOSE_FIGHT_BUTTON_2, MIN_COLOR_BG, MAX_COLOR_BG) > 0
        ) {
            return CLOSE_FIGHT_BUTTON_2
        }
        return null
    }

    private fun isLvlUp(gameInfo: GameInfo): Boolean {
        return ScreenUtil.isBetween(gameInfo, LVL_UP_OK_BUTTON_POINT, MIN_COLOR, MAX_COLOR)
    }

    private fun isFightEnded(gameInfo: GameInfo): Boolean {
        return gameInfo.eventStore.getLastEvent(MapComplementaryInformationsDataMessage::class.java) != null
    }

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val fightBoard = gameInfo.fightBoard
        val dofusBoard = gameInfo.dofusBoard
        initFight(gameInfo)

        val playerFighter = fightBoard.getPlayerFighter() ?: error("Player not found")

        playerFighter.statsById.putAll(gameInfo.playerBaseCharacteristics)
        val baseRange = FighterCharacteristic.RANGE.getFighterCharacteristicValue(playerFighter)
        val spells = gameInfo.character.spells
        val fightAI = FightAI(dofusBoard, fightBoard, playerFighter, baseRange, 0, spells, aiComplement)

        fightAI.selectStartCell()?.takeIf { it != playerFighter.cell }?.let {
            WaitUtil.sleep(800)
            MouseUtil.leftClick(gameInfo, it.getCenter())
            WaitUtil.sleep(800)
        }
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))
        WaitUtil.sleep(500)

        gameInfo.eventStore.clear(MapComplementaryInformationsDataMessage::class.java)
        gameInfo.eventStore.clear(SetCharacterRestrictionsMessage::class.java)
        KeyboardUtil.sendKey(gameInfo, KeyEvent.VK_F1, 0)
        waitForMessage(gameInfo, GameFightTurnStartPlayingMessage::class.java)

        while (!isFightEnded(gameInfo)) {
            MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo), 400)

            fightAI.onNewTurn()
            lateinit var nextOperation: FightOperation
            while (!isFightEnded(gameInfo) && fightAI.getNextOperation()?.also { nextOperation = it } != null) {
                val target = gameInfo.dofusBoard.getCell(nextOperation.targetCellId)
                if (nextOperation.type == FightOperationType.MOVE) {
                    processMove(gameInfo, target)
                } else if (nextOperation.type == FightOperationType.SPELL) {
                    castSpells(gameInfo, nextOperation.keys, target)
                }
                WaitUtil.sleep(500)
            }
            KeyboardUtil.sendKey(gameInfo, KeyEvent.VK_F1, 0)
            waitForMessage(gameInfo, GameFightTurnEndMessage::class.java)
            waitForMessage(gameInfo, GameFightTurnStartPlayingMessage::class.java, timeOutMillis = 2 * 60 * 1000)
        }

        WaitUtil.waitForEvents(gameInfo, SetCharacterRestrictionsMessage::class.java)
        gameInfo.eventStore.clearUntilFirst(SetCharacterRestrictionsMessage::class.java)
        WaitUtil.waitForEvent(gameInfo, BasicNoOperationMessage::class.java)

        gameInfo.fightBoard.resetFighters()

        if (isLvlUp(gameInfo)) {
            MouseUtil.leftClick(gameInfo, LVL_UP_OK_BUTTON_POINT)
            WaitUtil.waitUntil({ !isLvlUp(gameInfo) })
        }
        WaitUtil.sleep(800)
        if (!WaitUtil.waitUntil({ getCloseButtonLocation(gameInfo) != null })) {
            error("Close button not found")
        }
        val bounds = getCloseButtonLocation(gameInfo) ?: error("Close battle button not found")
        MouseUtil.leftClick(gameInfo, bounds.getCenter())
        return true
    }

    private fun processMove(gameInfo: GameInfo, target: DofusCell) {
        gameInfo.eventStore.clear(SequenceEndMessage::class.java)
        gameInfo.eventStore.clear(BasicNoOperationMessage::class.java)
        RetryUtil.tryUntilSuccess(
            { MouseUtil.tripleLeftClick(gameInfo, target.getCenter()) },
            { waitForSequenceCompleteEnd(gameInfo, 500) },
            20
        )
        waitForSequenceCompleteEnd(gameInfo, 5000)
    }

    private fun castSpells(gameInfo: GameInfo, keys: String, target: DofusCell) {
        for (c in keys) {
            castSpell(gameInfo, c, target)
            if (isFightEnded(gameInfo) || !gameInfo.fightBoard.isFighterHere(target)) {
                return
            }
        }
    }

    private fun castSpell(gameInfo: GameInfo, key: Char, target: DofusCell): Boolean {
        gameInfo.eventStore.clear(SequenceEndMessage::class.java)
        gameInfo.eventStore.clear(BasicNoOperationMessage::class.java)
        return RetryUtil.tryUntilSuccess(
            {
                KeyboardUtil.sendKey(gameInfo, KeyEvent.getExtendedKeyCodeForChar(key.code), 300)
                MouseUtil.tripleLeftClick(gameInfo, target.getCenter())
                waitForSequenceCompleteEnd(gameInfo, 4000)
            }, 4
        )
    }

    private fun waitForSequenceCompleteEnd(gameInfo: GameInfo, waitTime: Int): Boolean {
        return WaitUtil.waitUntil({ isFightEnded(gameInfo) || isSequenceComplete(gameInfo) }, waitTime)
    }

    private fun isSequenceComplete(gameInfo: GameInfo): Boolean {
        return gameInfo.eventStore.isAllEventsPresent(
            SequenceEndMessage::class.java,
            SequenceEndMessage::class.java,
            BasicNoOperationMessage::class.java,
            BasicNoOperationMessage::class.java
        )
    }

    private fun waitForMessage(
        gameInfo: GameInfo,
        eventClass: Class<out INetworkMessage>,
        timeOutMillis: Int = WaitUtil.DEFAULT_TIMEOUT_MILLIS
    ): Boolean {
        gameInfo.eventStore.clear(eventClass)
        return WaitUtil.waitUntil(
            { isFightEnded(gameInfo) || gameInfo.eventStore.getLastEvent(eventClass) != null },
            timeOutMillis
        )
    }

    private fun initFight(gameInfo: GameInfo) {
        val uiPoint = DofusUIPositionsManager.getBannerUiPosition(DofusUIPositionsManager.CONTEXT_FIGHT)
            ?: DefaultUIPositions.BANNER_UI_POSITION
        val uiPointRelative = ConverterUtil.toPointRelative(uiPoint)
        val deltaTopLeftPoint = REF_TOP_LEFT_POINT.opposite().getSum(uiPointRelative)
        val creatureModeBounds = REF_CREATURE_MODE_BUTTON_BOUNDS.getTranslation(deltaTopLeftPoint)
        val blockHelpBounds = REF_BLOCK_HELP_BUTTON_BOUNDS.getTranslation(deltaTopLeftPoint)
        val restrictToTeamBounds = REF_RESTRICT_TO_TEAM_BUTTON_BOUNDS.getTranslation(deltaTopLeftPoint)

        val fightOptionsReceived = WaitUtil.waitUntil(
            { getBlockHelpOptionValue(gameInfo) != null && getRestrictToTeamOptionValue(gameInfo) != null }
        )
        if (!fightOptionsReceived) {
            error("Fight option values not received")
        }

        WaitUtil.waitForEvent(gameInfo, GameEntitiesDispositionMessage::class.java)

        val fightersDisplayed = WaitUtil.waitUntil(
            { gameInfo.fightBoard.getPlayerFighter() != null && gameInfo.fightBoard.getEnemyFighters().isNotEmpty() }
        )
        if (!fightersDisplayed) {
            error("No fighters found in fight")
        }
        WaitUtil.sleep(1500)

        if (getBlockHelpOptionValue(gameInfo) == teamFight) {
            MouseUtil.leftClick(gameInfo, blockHelpBounds.getCenter())
        }
        if (teamFight && getRestrictToTeamOptionValue(gameInfo) == true) {
            MouseUtil.leftClick(gameInfo, restrictToTeamBounds.getCenter())
        }
        if (!gameInfo.isCreatureModeToggled) {
            if (!WaitUtil.waitUntil({ isCreatureModeActive(gameInfo, creatureModeBounds) }, 1000)) {
                MouseUtil.leftClick(gameInfo, creatureModeBounds.getCenter())
            }
            gameInfo.isCreatureModeToggled = true
        }

        gameInfo.eventStore.clearUntilLast(GameEntitiesDispositionMessage::class.java)
    }

    private fun isCreatureModeActive(gameInfo: GameInfo, creatureModeBounds: RectangleRelative): Boolean {
        return ScreenUtil.colorCount(gameInfo, creatureModeBounds, MIN_COLOR, MAX_COLOR) > 0
    }

    private fun getBlockHelpOptionValue(gameInfo: GameInfo): Boolean? {
        return getFightOptionValue(gameInfo, 2)
    }

    private fun getRestrictToTeamOptionValue(gameInfo: GameInfo): Boolean? {
        return getFightOptionValue(gameInfo, 1)
    }

    private fun getFightOptionValue(gameInfo: GameInfo, option: Int): Boolean? {
        return gameInfo.eventStore.getLastEvent(
            GameFightOptionStateUpdateMessage::class.java
        ) { it.option == option }?.state
    }

    override fun onStarted(): String {
        return "Fight started"
    }
}