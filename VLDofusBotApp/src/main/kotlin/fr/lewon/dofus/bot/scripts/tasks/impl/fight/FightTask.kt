package fr.lewon.dofus.bot.scripts.tasks.impl.fight

import fr.lewon.dofus.bot.core.d2o.managers.spell.SpellManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.ai.FightAI
import fr.lewon.dofus.bot.game.fight.ai.complements.AIComplement
import fr.lewon.dofus.bot.game.fight.ai.complements.DefaultAIComplement
import fr.lewon.dofus.bot.game.fight.ai.impl.DefaultFightAI
import fr.lewon.dofus.bot.game.fight.operations.FightOperation
import fr.lewon.dofus.bot.game.fight.operations.FightOperationType
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight.GameActionFightCastOnTargetRequestMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight.GameActionFightCastRequestMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.actions.sequence.SequenceEndMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.basic.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.GameEntitiesDispositionMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.GameMapMovementRequestMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.GameFightEndMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.GameFightOptionStateUpdateMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.GameFightTurnEndMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.GameFightTurnStartPlayingMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.initialization.SetCharacterRestrictionsMessage
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterSpellManager
import fr.lewon.dofus.bot.util.game.DofusColors
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.game.RetryUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.*
import fr.lewon.dofus.bot.util.network.info.GameInfo
import fr.lewon.dofus.bot.util.ui.UiUtil
import java.awt.event.KeyEvent

open class FightTask(
    private val aiComplement: AIComplement = DefaultAIComplement(),
    private val teamFight: Boolean = false
) : BooleanDofusBotTask() {

    companion object {

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


    private fun getLvlUpCloseButtonBounds(gameInfo: GameInfo): RectangleRelative? {
        val lvlUpUiElements = listOf(
            DofusUIElement.LVL_UP,
            DofusUIElement.LVL_UP_OMEGA,
            DofusUIElement.LVL_UP_WITH_SPELL
        )
        for (uiElement in lvlUpUiElements) {
            val closeButtonBounds = UiUtil.getContainerBounds(uiElement, "btn_close_main")
            if (ScreenUtil.colorCount(gameInfo, closeButtonBounds, MIN_COLOR, MAX_COLOR) != 0) {
                return closeButtonBounds
            }
        }
        return null
    }

    private fun isFightEnded(gameInfo: GameInfo): Boolean {
        return gameInfo.eventStore.getLastEvent(GameFightEndMessage::class.java) != null
                || gameInfo.eventStore.getLastEvent(MapComplementaryInformationsDataMessage::class.java) != null
    }

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val fightBoard = gameInfo.fightBoard
        val dofusBoard = gameInfo.dofusBoard
        initFight(gameInfo)

        val characterSpells = CharacterSpellManager.getSpells(gameInfo.character)
        val characterSpellBySpellLevel = HashMap<DofusSpellLevel, CharacterSpell>()
        for (characterSpell in characterSpells) {
            val spell = characterSpell.spellId?.let { SpellManager.getSpell(it) }
            if (spell != null) {
                for (level in spell.levels) {
                    characterSpellBySpellLevel[level] = characterSpell
                }
            }
        }

        val ai = getFightAI(dofusBoard, aiComplement)
        selectInitialPosition(gameInfo, fightBoard, ai)
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))

        gameInfo.eventStore.clear()
        KeyboardUtil.sendKey(gameInfo, KeyEvent.VK_F1, 0)
        waitForMessage(gameInfo, GameFightTurnStartPlayingMessage::class.java, 60 * 1000)

        ai.onFightStart(fightBoard)
        while (!isFightEnded(gameInfo)) {
            MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo), 400)

            ai.onNewTurn(fightBoard)
            var nextOperation: FightOperation = ai.getNextOperation(fightBoard, null)
            while (!isFightEnded(gameInfo) && nextOperation.type != FightOperationType.PASS_TURN) {
                val targetCellId = nextOperation.targetCellId ?: error("Target cell id can't be null")
                val target = gameInfo.dofusBoard.getCell(targetCellId)
                if (nextOperation.type == FightOperationType.MOVE) {
                    processMove(gameInfo, target)
                } else if (nextOperation.type == FightOperationType.SPELL) {
                    val spell = nextOperation.spell ?: error("Spell can't be null")
                    val characterSpell = characterSpellBySpellLevel[spell]
                        ?: error("No character spell found for spell level : ${spell.id}")
                    castSpell(gameInfo, characterSpell, target)
                }
                WaitUtil.sleep(500)
                if (fightBoard.getEnemyFighters().isEmpty()) {
                    break
                }
                nextOperation = ai.getNextOperation(fightBoard, nextOperation)
            }
            KeyboardUtil.sendKey(gameInfo, KeyEvent.VK_F1, 0)
            waitForMessage(gameInfo, GameFightTurnEndMessage::class.java)
            waitForMessage(gameInfo, GameFightTurnStartPlayingMessage::class.java, timeOutMillis = 3 * 60 * 1000)
        }

        WaitUtil.waitForEvents(gameInfo, SetCharacterRestrictionsMessage::class.java)
        gameInfo.eventStore.clearUntilFirst(SetCharacterRestrictionsMessage::class.java)
        WaitUtil.waitForEvent(gameInfo, BasicNoOperationMessage::class.java)

        gameInfo.fightBoard.resetFighters()

        WaitUtil.sleep(800)
        if (!WaitUtil.waitUntil { getCloseButtonLocation(gameInfo) != null || getLvlUpCloseButtonBounds(gameInfo) != null }) {
            error("Close button not found")
        }
        val lvlUpCloseButtonBounds = getLvlUpCloseButtonBounds(gameInfo)
        if (lvlUpCloseButtonBounds != null) {
            MouseUtil.leftClick(gameInfo, lvlUpCloseButtonBounds.getCenter())
            WaitUtil.waitUntil { getLvlUpCloseButtonBounds(gameInfo) == null }
        }
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo), 500)
        KeyboardUtil.sendKey(gameInfo, KeyEvent.VK_ESCAPE)
        return true
    }

    protected open fun getFightAI(dofusBoard: DofusBoard, aiComplement: AIComplement): FightAI {
        return DefaultFightAI(dofusBoard, aiComplement)
    }

    protected open fun selectInitialPosition(gameInfo: GameInfo, fightBoard: FightBoard, ai: FightAI) {
        val playerFighter = fightBoard.getPlayerFighter() ?: error("Player not found")
        ai.selectStartCell(fightBoard)?.takeIf { it != playerFighter.cell }?.let {
            WaitUtil.sleep(500)
            MouseUtil.leftClick(gameInfo, it.getCenter())
        }
    }

    private fun processMove(gameInfo: GameInfo, target: DofusCell) {
        gameInfo.eventStore.clear()
        RetryUtil.tryUntilSuccess(
            { MouseUtil.doubleLeftClick(gameInfo, target.getCenter()) },
            { waitUntilMoveRequested(gameInfo) },
            4
        ) ?: error("Couldn't request move")
        waitForSequenceCompleteEnd(gameInfo)
    }

    private fun waitUntilMoveRequested(gameInfo: GameInfo): Boolean = WaitUtil.waitUntil(2000) {
        gameInfo.eventStore.getLastEvent(GameMapMovementRequestMessage::class.java) != null
    }

    private fun castSpell(gameInfo: GameInfo, characterSpell: CharacterSpell, target: DofusCell) {
        gameInfo.eventStore.clear()
        RetryUtil.tryUntilSuccess(
            {
                val keyEvent = KeyEvent.getExtendedKeyCodeForChar(characterSpell.key.code)
                KeyboardUtil.sendKey(gameInfo, keyEvent, 300, characterSpell.ctrlModifier)
                MouseUtil.leftClick(gameInfo, target.getCenter())
            },
            { waitUntilSpellCastRequested(gameInfo) },
            4
        ) ?: error("Couldn't cast spell")
    }

    private fun waitUntilSpellCastRequested(gameInfo: GameInfo): Boolean = WaitUtil.waitUntil(2000) {
        gameInfo.eventStore.getLastEvent(GameActionFightCastOnTargetRequestMessage::class.java) != null
                || gameInfo.eventStore.getLastEvent(GameActionFightCastRequestMessage::class.java) != null
    }

    private fun waitForSequenceCompleteEnd(gameInfo: GameInfo): Boolean = WaitUtil.waitUntil(5000) {
        isFightEnded(gameInfo) || isSequenceComplete(gameInfo)
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
        eventClass: Class<out NetworkMessage>,
        timeOutMillis: Int = WaitUtil.DEFAULT_TIMEOUT_MILLIS
    ): Boolean = WaitUtil.waitUntil(timeOutMillis) {
        isFightEnded(gameInfo) || gameInfo.eventStore.getLastEvent(eventClass) != null
    }

    private fun initFight(gameInfo: GameInfo) {
        val uiPoint = DofusUIElement.BANNER.getPosition(true)
        val uiPointRelative = ConverterUtil.toPointRelative(uiPoint)
        val deltaTopLeftPoint = REF_TOP_LEFT_POINT.opposite().getSum(uiPointRelative)
        val creatureModeBounds = REF_CREATURE_MODE_BUTTON_BOUNDS.getTranslation(deltaTopLeftPoint)
        val blockHelpBounds = REF_BLOCK_HELP_BUTTON_BOUNDS.getTranslation(deltaTopLeftPoint)
        val restrictToTeamBounds = REF_RESTRICT_TO_TEAM_BUTTON_BOUNDS.getTranslation(deltaTopLeftPoint)

        val fightOptionsReceived = WaitUtil.waitUntil {
            getBlockHelpOptionValue(gameInfo) != null && getRestrictToTeamOptionValue(gameInfo) != null
        }

        if (!fightOptionsReceived) {
            error("Fight option values not received")
        }

        WaitUtil.waitUntil(2000) {
            gameInfo.fightBoard.getPlayerFighter() != null && gameInfo.fightBoard.getEnemyFighters().isNotEmpty()
        }
        WaitUtil.sleep(800)

        if (getBlockHelpOptionValue(gameInfo) == teamFight) {
            MouseUtil.leftClick(gameInfo, blockHelpBounds.getCenter())
        }
        if (teamFight && getRestrictToTeamOptionValue(gameInfo) == true) {
            MouseUtil.leftClick(gameInfo, restrictToTeamBounds.getCenter())
        }
        if (!gameInfo.isCreatureModeToggled) {
            if (!WaitUtil.waitUntil(1000) { isCreatureModeActive(gameInfo, creatureModeBounds) }) {
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