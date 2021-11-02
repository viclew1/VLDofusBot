package fr.lewon.dofus.bot.scripts.tasks.impl.fight

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.DofusUIPositionsManager
import fr.lewon.dofus.bot.core.manager.ui.UIPoint
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.game.fight.*
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination
import fr.lewon.dofus.bot.model.characters.spells.SpellType
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightTurnEndMessage
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightTurnStartPlayingMessage
import fr.lewon.dofus.bot.sniffer.model.messages.fight.SequenceEndMessage
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.game.CharacteristicUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.*
import java.awt.event.KeyEvent

class FightTask : DofusBotTask<Boolean>() {

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

        private val MIN_COLOR = AppColors.HIGHLIGHT_COLOR_MIN
        private val MAX_COLOR = AppColors.HIGHLIGHT_COLOR_MAX
        private val MIN_COLOR_CROSS = AppColors.UI_BANNER_BLACK_COLOR_MIN
        private val MAX_COLOR_CROSS = AppColors.UI_BANNER_BLACK_COLOR_MAX
        private val MIN_COLOR_BG = AppColors.UI_BANNER_GREY_COLOR_MIN
        private val MAX_COLOR_BG = AppColors.UI_BANNER_GREY_COLOR_MAX

    }

    private lateinit var readyButtonBounds: RectangleRelative
    private lateinit var creatureModeBounds: RectangleRelative
    private lateinit var blockHelpBounds: RectangleRelative

    private fun getCloseButtonLocation(): RectangleRelative? {
        if (ScreenUtil.colorCount(CLOSE_FIGHT_BUTTON_1, MIN_COLOR_CROSS, MAX_COLOR_CROSS) > 0
            && ScreenUtil.colorCount(CLOSE_FIGHT_BUTTON_1, MIN_COLOR_BG, MAX_COLOR_BG) > 0
        ) {
            return CLOSE_FIGHT_BUTTON_1
        }
        if (ScreenUtil.colorCount(CLOSE_FIGHT_BUTTON_2, MIN_COLOR_CROSS, MAX_COLOR_CROSS) > 0
            && ScreenUtil.colorCount(CLOSE_FIGHT_BUTTON_2, MIN_COLOR_BG, MAX_COLOR_BG) > 0
        ) {
            return CLOSE_FIGHT_BUTTON_2
        }
        return null
    }

    private fun isLvlUp(): Boolean {
        return ScreenUtil.isBetween(LVL_UP_OK_BUTTON_POINT, MIN_COLOR, MAX_COLOR)
    }

    private fun isFightEnded(): Boolean {
        return EventStore.getLastEvent(MapComplementaryInformationsDataMessage::class.java) != null
    }

    override fun execute(logItem: LogItem): Boolean {
        val character = CharacterManager.getCurrentCharacter() ?: error("No current character defined")
        val preMoveBuffCombination = SpellCombination(SpellType.MP_BUFF, "121", 0, 0, true, false, 4, 4, 1, 5, -2)
        val losSpellCombo = SpellCombination(SpellType.ATTACK, "33", 2, 11, true, false, 0, 6, 2, 0, 5)
        val nonLosSpellCombo = SpellCombination(SpellType.ATTACK, "", -1, -1, false, false, 0, 0, 0, 0)
        val contactSpellCombo = SpellCombination(SpellType.ATTACK, "44", 1, 1, true, true, 0, 8, 2, 0, 2)
        val gapCloserCombination = SpellCombination(SpellType.GAP_CLOSER, "", -1, -1, false, false, 0, 0, 0, 0)
        var preMoveBuffCd = 0

        val fightBoard = GameInfo.fightBoard
        initFight()
        WaitUtil.waitUntil({ fightBoard.getPlayerFighter() != null && fightBoard.getEnemyFighters().isNotEmpty() })

        val playerFighter = fightBoard.getPlayerFighter() ?: error("Player not found")
        fightBoard.startCells
            .minByOrNull { fightBoard.getPathLength(it, fightBoard.closestEnemyPosition) ?: Int.MAX_VALUE }
            ?.takeIf { it != playerFighter.fightCell }
            ?.let { MouseUtil.leftClick(it.getCenter()) }

        EventStore.clear(MapComplementaryInformationsDataMessage::class.java)
        MouseUtil.leftClick(readyButtonBounds.getCenter(), false, 0)
        waitForMessage(GameFightTurnStartPlayingMessage::class.java)
        while (!isFightEnded()) {
            MouseUtil.leftClick(ConfigManager.config.mouseRestPos, false, 400)

            var playerPos = playerFighter.fightCell
            val enemyFighter = fightBoard.getFighter(fightBoard.closestEnemyPosition) ?: error("No enemy left")

            val closestEnemyDist = fightBoard.getDist(playerPos, fightBoard.closestEnemyPosition)
                ?: Int.MAX_VALUE

            if (--preMoveBuffCd <= 0 && closestEnemyDist > 1) {
                castSpells(preMoveBuffCombination.keys, playerPos)
                preMoveBuffCd = preMoveBuffCombination.cooldown
            }

            val mp = getFighterMp(playerFighter)
            val enemyMp = getFighterMp(enemyFighter)

            val fightAI = FightAI(mp, enemyMp, fightBoard, losSpellCombo, nonLosSpellCombo, contactSpellCombo, 1)

            if (fightBoard.getDist(playerPos, fightBoard.closestEnemyPosition) ?: Int.MAX_VALUE <= 1) {
                castSpells(contactSpellCombo.keys, fightBoard.closestEnemyPosition)
            } else {
                moveToBestCell(playerPos, fightAI)
                playerPos = playerFighter.fightCell
                useGapClosers(playerPos, fightAI, gapCloserCombination)
                useAttacks(playerPos, fightBoard, losSpellCombo, nonLosSpellCombo, contactSpellCombo)
            }

            MouseUtil.leftClick(readyButtonBounds.getCenter(), false, 0)
            waitForMessage(GameFightTurnEndMessage::class.java)
            waitForMessage(GameFightTurnStartPlayingMessage::class.java)
        }

        GameInfo.fightBoard.resetFighters()

        if (isLvlUp()) {
            MouseUtil.leftClick(LVL_UP_OK_BUTTON_POINT)
            WaitUtil.waitUntil({ !isLvlUp() })
        }
        val bounds = getCloseButtonLocation() ?: error("Close battle button not found")
        MouseUtil.leftClick(bounds.getCenter())
        return true
    }

    private fun getFighterMp(playerFighter: Fighter): Int {
        return getFighterCharacteristicValue(playerFighter, FighterCharacteristic.MP)
    }

    private fun getFighterAp(playerFighter: Fighter): Int {
        return getFighterCharacteristicValue(playerFighter, FighterCharacteristic.AP)
    }

    private fun getFighterCharacteristicValue(fighter: Fighter, fighterCharacteristic: FighterCharacteristic): Int {
        println(
            "${fighterCharacteristic.keyword}:${fighter.id} = ${
                CharacteristicUtil.getCharacteristicValue(
                    fighterCharacteristic,
                    fighter.statsById
                )
            }"
        )
        return CharacteristicUtil.getCharacteristicValue(fighterCharacteristic, fighter.statsById)
            ?: error("Characteristic not found for fighter [${fighter.id}] : ${fighterCharacteristic.keyword}")
    }

    private fun useAttacks(
        playerPosition: FightCell,
        fightBoard: FightBoard,
        losSpellCombination: SpellCombination,
        nonLosSpellCombination: SpellCombination,
        contactSpellCombination: SpellCombination
    ) {
        val dist = fightBoard.getDist(playerPosition, fightBoard.closestEnemyPosition) ?: Int.MAX_VALUE
        val los = fightBoard.lineOfSight(playerPosition, fightBoard.closestEnemyPosition)
        val attacks = when {
            dist <= 1 -> contactSpellCombination.keys
            los && dist in losSpellCombination.minRange..losSpellCombination.maxRange -> losSpellCombination.keys
            dist in nonLosSpellCombination.minRange..nonLosSpellCombination.maxRange -> nonLosSpellCombination.keys
            else -> ""
        }
        castSpells(attacks, fightBoard.closestEnemyPosition)
    }

    private fun useGapClosers(playerPosition: FightCell, fightAI: FightAI, gapCloserSpell: SpellCombination) {
        if (gapCloserSpell.keys.isNotEmpty()) {
            val minRange = gapCloserSpell.minRange
            val maxRange = gapCloserSpell.maxRange
            val bestCell = fightAI.selectBestTpDest(minRange, maxRange)
            if (bestCell != playerPosition) {
                castSpells(gapCloserSpell.keys, bestCell)
            }
        }
    }

    private fun moveToBestCell(playerPosition: FightCell, fightAI: FightAI) {
        fightAI.selectBestMoveDest().takeIf { it != playerPosition }?.let {
            MouseUtil.leftClick(it.getCenter(), false, 0)
            return waitForSequenceCompleteEnd()
        }
    }

    private fun castSpells(keys: String, target: FightCell) {
        for (c in keys) {
            castSpell(c, target)
            if (isFightEnded()) {
                return
            }
        }
    }

    private fun castSpell(key: Char, target: FightCell) {
        KeyboardUtil.sendKey(KeyEvent.getExtendedKeyCodeForChar(key.code), 150)
        MouseUtil.leftClick(target.getCenter(), false, 0)
        return waitForSequenceCompleteEnd()
    }

    private fun waitForSequenceCompleteEnd() {
        if (waitForMessage(SequenceEndMessage::class.java, timeOutMillis = 2000)) {
            waitForMessage(BasicNoOperationMessage::class.java, timeOutMillis = 2000)
        }
    }

    private fun waitForMessage(
        eventClass: Class<out INetworkMessage>,
        timeOutMillis: Int = ConfigManager.config.globalTimeout * 1000
    ): Boolean {
        EventStore.clear(eventClass)
        return WaitUtil.waitUntil({ isFightEnded() || EventStore.getLastEvent(eventClass) != null }, timeOutMillis)
    }

    private fun initFight() {
        val uiPoint = DofusUIPositionsManager.getBannerUiPosition(DofusUIPositionsManager.CONTEXT_FIGHT)
            ?: UIPoint(400f, 909f)
        val uiPointRelative = ConverterUtil.toPointRelative(uiPoint)
        val deltaTopLeftPoint = REF_TOP_LEFT_POINT.opposite().getSum(uiPointRelative)
        readyButtonBounds = REF_READY_BUTTON_BOUNDS.getTranslation(deltaTopLeftPoint)
        creatureModeBounds = REF_CREATURE_MODE_BUTTON_BOUNDS.getTranslation(deltaTopLeftPoint)
        blockHelpBounds = REF_BLOCK_HELP_BUTTON_BOUNDS.getTranslation(deltaTopLeftPoint)

        if (!WaitUtil.waitUntil({ ScreenUtil.colorCount(readyButtonBounds, MIN_COLOR, MAX_COLOR) > 0 })) {
            error("Couldn't find READY button")
        }
        if (ScreenUtil.colorCount(creatureModeBounds, MIN_COLOR, MAX_COLOR) == 0) {
            MouseUtil.leftClick(creatureModeBounds.getCenter())
        }
        if (ScreenUtil.colorCount(blockHelpBounds, MIN_COLOR, MAX_COLOR) == 0) {
            MouseUtil.leftClick(blockHelpBounds.getCenter())
        }
    }


    override fun onStarted(): String {
        return "Fight started"
    }
}