package fr.lewon.dofus.bot.scripts.tasks.impl.fight

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.DofusUIPositionsManager
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.FightAI
import fr.lewon.dofus.bot.game.fight.FighterCharacteristic
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination
import fr.lewon.dofus.bot.model.characters.spells.SpellType
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightTurnEndMessage
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightTurnStartPlayingMessage
import fr.lewon.dofus.bot.sniffer.model.messages.fight.SequenceEndMessage
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.game.DefaultUIPositions
import fr.lewon.dofus.bot.util.game.DofusColors
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.game.RetryUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.*
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.event.KeyEvent

class FightTask : BooleanDofusBotTask() {

    companion object {

        private val LVL_UP_OK_BUTTON_POINT = PointRelative(0.47435898f, 0.6868327f)
        private val REF_TOP_LEFT_POINT = PointRelative(0.15609138f, 0.88906497f)

        private val REF_READY_BUTTON_BOUNDS = RectangleRelative.build(
            PointRelative(0.6294416f, 0.9001585f),
            PointRelative(0.7385787f, 0.9540412f)
        )

        private val REF_CREATURE_MODE_BUTTON_BOUNDS = RectangleRelative.build(
            PointRelative(0.6497462f, 0.9667195f),
            PointRelative(0.66497463f, 0.9857369f)
        )

        private val REF_BLOCK_HELP_BUTTON_BOUNDS = RectangleRelative.build(
            PointRelative(0.66243654f, 0.86529315f),
            PointRelative(0.67893404f, 0.8827259f)
        )

        private val CLOSE_FIGHT_BUTTON_1 = RectangleRelative.build(
            PointRelative(0.9387097f, 0.27620968f),
            PointRelative(0.95967746f, 0.30040324f)
        )

        private val CLOSE_FIGHT_BUTTON_2 = RectangleRelative.build(
            PointRelative(0.7177419f, 0.6895161f),
            PointRelative(0.7322581f, 0.7076613f)
        )

        private val MIN_COLOR = DofusColors.HIGHLIGHT_COLOR_MIN
        private val MAX_COLOR = DofusColors.HIGHLIGHT_COLOR_MAX
        private val MIN_COLOR_CROSS = DofusColors.UI_BANNER_BLACK_COLOR_MIN
        private val MAX_COLOR_CROSS = DofusColors.UI_BANNER_BLACK_COLOR_MAX
        private val MIN_COLOR_BG = DofusColors.UI_BANNER_GREY_COLOR_MIN
        private val MAX_COLOR_BG = DofusColors.UI_BANNER_GREY_COLOR_MAX

    }

    private lateinit var readyButtonBounds: RectangleRelative
    private lateinit var creatureModeBounds: RectangleRelative
    private lateinit var blockHelpBounds: RectangleRelative
    private var preMoveBuffCd = 0

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
        return EventStore.getLastEvent(MapComplementaryInformationsDataMessage::class.java, gameInfo.snifferId) != null
    }

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        val preMoveBuffCombination =
            SpellCombination(SpellType.MP_BUFF, "121", 0, 0, true, false, false, 4, 4, 1, 5, -2)
        val losSpells = SpellCombination(SpellType.ATTACK, "33", 2, 6, true, false, true, 0, 6, 2, 0, 5)
        val nonLosSpells = SpellCombination(SpellType.ATTACK, "", -1, -1, false, false, false, 0, 0, 0, 0)
        val contactSpells = SpellCombination(SpellType.ATTACK, "44", 1, 1, true, true, false, 0, 8, 2, 0, 2)
        val gapCloserCombination = SpellCombination(SpellType.GAP_CLOSER, "", -1, -1, false, false, false, 0, 0, 0, 0)
        preMoveBuffCd = 0

        val fightBoard = gameInfo.fightBoard
        val dofusBoard = gameInfo.dofusBoard
        initFight(gameInfo, cancellationToken)
        WaitUtil.waitUntil(
            { fightBoard.getPlayerFighter() != null && fightBoard.getEnemyFighters().isNotEmpty() }, cancellationToken
        )

        val playerFighter = fightBoard.getPlayerFighter() ?: error("Player not found")
        dofusBoard.startCells
            .minByOrNull { dofusBoard.getPathLength(it, fightBoard.closestEnemyPosition) ?: Int.MAX_VALUE }
            ?.takeIf { it != playerFighter.cell }
            ?.let { MouseUtil.leftClick(gameInfo, it.getCenter()) }

        playerFighter.statsById.putAll(gameInfo.playerBaseCharacteristics)
        val baseRange = FighterCharacteristic.RANGE.getFighterCharacteristicValue(playerFighter)

        EventStore.clear(MapComplementaryInformationsDataMessage::class.java, gameInfo.snifferId)
        KeyboardUtil.sendKey(gameInfo, KeyEvent.VK_F1, 0)
        waitForMessage(gameInfo, GameFightTurnStartPlayingMessage::class.java, cancellationToken)
        while (!isFightEnded(gameInfo)) {
            MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo), 400)

            var playerPos = playerFighter.cell
            val enemyFighter = fightBoard.getFighter(fightBoard.closestEnemyPosition) ?: error("No enemy left")

            --preMoveBuffCd

            val mp = FighterCharacteristic.MP.getFighterCharacteristicValue(playerFighter)
            val enemyMp = FighterCharacteristic.MP.getFighterCharacteristicValue(enemyFighter)

            val fightAI =
                FightAI(mp, baseRange, enemyMp, dofusBoard, fightBoard, losSpells, nonLosSpells, contactSpells, 1)

            if ((dofusBoard.getDist(playerPos, fightBoard.closestEnemyPosition) ?: Int.MAX_VALUE) <= 1) {
                castSpells(gameInfo, contactSpells.keys, fightBoard.closestEnemyPosition, cancellationToken)
            } else {
                moveToBestCell(gameInfo, playerPos, fightAI, preMoveBuffCombination, cancellationToken)
                playerPos = playerFighter.cell
                useGapClosers(gameInfo, playerPos, fightAI, gapCloserCombination, cancellationToken)
                useAttacks(gameInfo, playerPos, baseRange, losSpells, nonLosSpells, contactSpells, cancellationToken)
            }

            KeyboardUtil.sendKey(gameInfo, KeyEvent.VK_F1, 0)
            waitForMessage(gameInfo, GameFightTurnEndMessage::class.java, cancellationToken)
            waitForMessage(gameInfo, GameFightTurnStartPlayingMessage::class.java, cancellationToken)
        }

        gameInfo.fightBoard.resetFighters()

        if (isLvlUp(gameInfo)) {
            MouseUtil.leftClick(gameInfo, LVL_UP_OK_BUTTON_POINT)
            WaitUtil.waitUntil({ !isLvlUp(gameInfo) }, cancellationToken)
        }
        if (!WaitUtil.waitUntil({ getCloseButtonLocation(gameInfo) != null }, cancellationToken)) {
            return false
        }
        val bounds = getCloseButtonLocation(gameInfo) ?: error("Close battle button not found")
        MouseUtil.leftClick(gameInfo, bounds.getCenter())
        return true
    }

    private fun useAttacks(
        gameInfo: GameInfo,
        playerPosition: DofusCell,
        range: Int,
        losSpellCombination: SpellCombination,
        nonLosSpellCombination: SpellCombination,
        contactSpellCombination: SpellCombination,
        cancellationToken: CancellationToken
    ) {
        val fightBoard = gameInfo.fightBoard
        val dofusBoard = gameInfo.dofusBoard
        val dist = dofusBoard.getDist(playerPosition, fightBoard.closestEnemyPosition) ?: Int.MAX_VALUE
        val los = fightBoard.lineOfSight(playerPosition, fightBoard.closestEnemyPosition)
        var losMaxRange = losSpellCombination.maxRange
        if (losSpellCombination.modifiableRange) {
            losMaxRange += range
        }
        val attacks = when {
            dist <= 1 -> contactSpellCombination.keys
            los && dist in losSpellCombination.minRange..losMaxRange -> losSpellCombination.keys
            dist in nonLosSpellCombination.minRange..nonLosSpellCombination.maxRange -> nonLosSpellCombination.keys
            else -> ""
        }
        castSpells(gameInfo, attacks, fightBoard.closestEnemyPosition, cancellationToken)
    }

    private fun useGapClosers(
        gameInfo: GameInfo,
        playerPosition: DofusCell,
        fightAI: FightAI,
        gapCloserSpell: SpellCombination,
        cancellationToken: CancellationToken
    ) {
        if (gapCloserSpell.keys.isNotEmpty()) {
            val minRange = gapCloserSpell.minRange
            val maxRange = gapCloserSpell.maxRange
            val bestCell = fightAI.selectBestTpDest(minRange, maxRange)
            if (bestCell != playerPosition) {
                castSpells(gameInfo, gapCloserSpell.keys, bestCell, cancellationToken)
            }
        }
    }

    private fun moveToBestCell(
        gameInfo: GameInfo,
        playerPosition: DofusCell,
        fightAI: FightAI,
        preMoveBuffCombination: SpellCombination,
        cancellationToken: CancellationToken
    ): Boolean {
        val potentialMpBuff = if (preMoveBuffCd <= 0) preMoveBuffCombination.amount else 0
        fightAI.selectBestMoveDest(potentialMpBuff).takeIf { it.first != playerPosition }?.let {
            if (it.second) {
                useMpBuff(gameInfo, preMoveBuffCombination, cancellationToken)
            }
            processMove(gameInfo, it.first, cancellationToken)
        }
        return true
    }

    private fun useMpBuff(
        gameInfo: GameInfo,
        preMoveBuffCombination: SpellCombination,
        cancellationToken: CancellationToken
    ) {
        val playerPos = gameInfo.fightBoard.getPlayerFighter()?.cell ?: error("Player cell not found")
        val closestEnemyDist = gameInfo.dofusBoard.getDist(playerPos, gameInfo.fightBoard.closestEnemyPosition)
            ?: Int.MAX_VALUE
        if (--preMoveBuffCd <= 0 && closestEnemyDist > 1) {
            castSpells(gameInfo, preMoveBuffCombination.keys, playerPos, cancellationToken)
            preMoveBuffCd = preMoveBuffCombination.cooldown
        }
    }

    private fun processMove(gameInfo: GameInfo, target: DofusCell, cancellationToken: CancellationToken) {
        EventStore.clear(SequenceEndMessage::class.java, gameInfo.snifferId)
        EventStore.clear(BasicNoOperationMessage::class.java, gameInfo.snifferId)
        RetryUtil.tryUntilSuccess(
            { MouseUtil.tripleLeftClick(gameInfo, target.getCenter()) },
            { WaitUtil.waitUntil({ isSequenceComplete(gameInfo) }, cancellationToken, 500) },
            20
        )
        WaitUtil.waitUntil({ isSequenceComplete(gameInfo) }, cancellationToken, 5000)
    }

    private fun castSpells(gameInfo: GameInfo, keys: String, target: DofusCell, cancellationToken: CancellationToken) {
        for (c in keys) {
            RetryUtil.tryUntilSuccess({ castSpell(gameInfo, c, target, cancellationToken) }, 3)
            if (isFightEnded(gameInfo) || !gameInfo.fightBoard.isFighterHere(target)) {
                return
            }
        }
    }

    private fun castSpell(
        gameInfo: GameInfo,
        key: Char,
        target: DofusCell,
        cancellationToken: CancellationToken
    ): Boolean {
        KeyboardUtil.sendKey(gameInfo, KeyEvent.getExtendedKeyCodeForChar(key.code), 300)
        MouseUtil.doubleLeftClick(gameInfo, target.getCenter())
        return waitForSequenceCompleteEnd(gameInfo, cancellationToken, 3000)
    }

    private fun waitForSequenceCompleteEnd(
        gameInfo: GameInfo,
        cancellationToken: CancellationToken,
        waitTime: Int
    ): Boolean {
        EventStore.clear(SequenceEndMessage::class.java, gameInfo.snifferId)
        EventStore.clear(BasicNoOperationMessage::class.java, gameInfo.snifferId)
        return WaitUtil.waitUntil(
            { isFightEnded(gameInfo) || isSequenceComplete(gameInfo) },
            cancellationToken,
            waitTime
        )
    }

    private fun isSequenceComplete(gameInfo: GameInfo): Boolean {
        return EventStore.isAllEventsPresent(
            gameInfo.snifferId,
            SequenceEndMessage::class.java,
            SequenceEndMessage::class.java,
            BasicNoOperationMessage::class.java,
            BasicNoOperationMessage::class.java
        )
    }

    private fun waitForMessage(
        gameInfo: GameInfo,
        eventClass: Class<out INetworkMessage>,
        cancellationToken: CancellationToken,
        timeOutMillis: Int = WaitUtil.DEFAULT_TIMEOUT_MILLIS
    ): Boolean {
        EventStore.clear(eventClass, gameInfo.snifferId)
        return WaitUtil.waitUntil(
            { isFightEnded(gameInfo) || EventStore.getLastEvent(eventClass, gameInfo.snifferId) != null },
            cancellationToken,
            timeOutMillis
        )
    }

    private fun initFight(gameInfo: GameInfo, cancellationToken: CancellationToken) {
        val uiPoint = DofusUIPositionsManager.getBannerUiPosition(DofusUIPositionsManager.CONTEXT_FIGHT)
            ?: DefaultUIPositions.BANNER_UI_POSITION
        val uiPointRelative = ConverterUtil.toPointRelative(uiPoint)
        val deltaTopLeftPoint = REF_TOP_LEFT_POINT.opposite().getSum(uiPointRelative)
        readyButtonBounds = REF_READY_BUTTON_BOUNDS.getTranslation(deltaTopLeftPoint)
        creatureModeBounds = REF_CREATURE_MODE_BUTTON_BOUNDS.getTranslation(deltaTopLeftPoint)
        blockHelpBounds = REF_BLOCK_HELP_BUTTON_BOUNDS.getTranslation(deltaTopLeftPoint)

        val readyButtonFound = WaitUtil.waitUntil(
            { ScreenUtil.colorCount(gameInfo, readyButtonBounds, MIN_COLOR, MAX_COLOR) > 0 }, cancellationToken
        )
        if (!readyButtonFound) {
            error("Couldn't find READY button")
        }
        if (ScreenUtil.colorCount(gameInfo, creatureModeBounds, MIN_COLOR, MAX_COLOR) == 0) {
            MouseUtil.leftClick(gameInfo, creatureModeBounds.getCenter())
        }
        if (ScreenUtil.colorCount(gameInfo, blockHelpBounds, MIN_COLOR, MAX_COLOR) == 0) {
            MouseUtil.leftClick(gameInfo, blockHelpBounds.getCenter())
        }
    }


    override fun onStarted(): String {
        return "Fight started"
    }
}