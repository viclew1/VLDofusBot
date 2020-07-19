package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameterType
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.DTBConfigManager
import fr.lewon.dofus.bot.util.GameInfoUtil
import fr.lewon.dofus.bot.util.RobotUtil
import fr.lewon.dofus.bot.util.fight.FightAI
import fr.lewon.dofus.bot.util.fight.FightBoard
import fr.lewon.dofus.bot.util.fight.FightCellType
import fr.lewon.dofus.bot.util.fight.FightColors
import java.awt.Rectangle
import java.awt.event.KeyEvent

object FightScript : DofusBotScript("Fight") {

    private val preMoveBuffParameter = DofusBotScriptParameter(
        "Pre move buffs",
        "Hotkeys to the buff spells to use before playing the turn",
        "",
        DofusBotScriptParameterType.STRING
    )
    private val losAttacksParameter = DofusBotScriptParameter(
        "LOS attacks",
        "Hotkeys to the attacks to use if you have a line of sight with the enemy",
        "",
        DofusBotScriptParameterType.STRING
    )
    private val minRangeParameter = DofusBotScriptParameter(
        "Min range",
        "Min range of the attack you wish to use",
        "0",
        DofusBotScriptParameterType.INTEGER
    )
    private val maxRangeParameter = DofusBotScriptParameter(
        "Max range",
        "Max range of the attack you wish to use",
        "10",
        DofusBotScriptParameterType.INTEGER
    )
    private val nonLosAttacksParameter = DofusBotScriptParameter(
        "Non LOS attacks",
        "Hotkeys to the attacks to use if you don't have a line of sight with the enemy",
        "",
        DofusBotScriptParameterType.STRING
    )
    private val contactAttacksParameter = DofusBotScriptParameter(
        "Contact attacks",
        "Hotkeys to the attacks to use if you are at the enemy contact",
        "",
        DofusBotScriptParameterType.STRING
    )
    private val gapCloserParameter = DofusBotScriptParameter(
        "Gap closer",
        "Hotkey to the gap closer to user to approach the enemy. Must be a single key",
        "",
        DofusBotScriptParameterType.STRING
    )
    private val gapCloserMinRangeParameter = DofusBotScriptParameter(
        "Gap closer min range",
        "Min range of the gap closer",
        "1",
        DofusBotScriptParameterType.INTEGER
    )
    private val gapCloserMaxRangeParameter = DofusBotScriptParameter(
        "Gap closer max range",
        "Max range of the gap closer",
        "5",
        DofusBotScriptParameterType.INTEGER
    )

    private var turnsPassed: Int = 0

    override fun getParameters(): List<DofusBotScriptParameter> {
        return listOf(
            preMoveBuffParameter,
            losAttacksParameter,
            minRangeParameter,
            maxRangeParameter,
            nonLosAttacksParameter,
            contactAttacksParameter,
            gapCloserParameter,
            gapCloserMinRangeParameter,
            gapCloserMaxRangeParameter
        )
    }

    override fun getStats(): List<Pair<String, String>> {
        return listOf(
            Pair("Turns passed", "$turnsPassed")
        )
    }

    override fun getDescription(): String {
        return "Clicks on fight button and fights a chest. The AI starts its turn by using its gap closer (${gapCloserParameter.value}) toward enemy. Then, it casts buffs on itself (${preMoveBuffParameter.value}), then moves, then casts the LOS attacks on the enemy (${losAttacksParameter.value}) if there is a line of sight, else casts nonLOS attacks (${nonLosAttacksParameter.value})"
    }

    override fun doExecute(
        controller: DofusTreasureBotGUIController,
        logItem: LogItem?,
        parameters: Map<String, DofusBotScriptParameter>
    ) {
        val preMoveBuffs = preMoveBuffParameter.value
        val losAttacks = losAttacksParameter.value
        val minRange = minRangeParameter.value.toInt()
        val maxRange = maxRangeParameter.value.toInt()
        val nonLosAttacks = nonLosAttacksParameter.value
        val contactAttacks = contactAttacksParameter.value
        val gapCloser = gapCloserParameter.value
        val gapCloserMinRange = if (gapCloser.isNotEmpty()) gapCloserMinRangeParameter.value.toInt() else 0
        val gapCloserMaxRange = if (gapCloser.isNotEmpty()) gapCloserMaxRangeParameter.value.toInt() else 0

        execTimeoutOpe({ }, { imgFound("fight/ready.png") })

        if (imgFound("fight/creature_mode.png", 0.9)) {
            click("fight/creature_mode.png")
        }
        getSubImage("fight/block_help.png")?.let {
            val blockHelpColorCount =
                GameInfoUtil.colorCount(it, Rectangle(0, 0, it.width, it.height), FightColors.lockHelpActiveColors)
            if (blockHelpColorCount < 15) {
                click("fight/block_help.png")
            }
        }

        var fightBoard: FightBoard? = null
        val start = System.currentTimeMillis()
        while (getTime() - start < DTBConfigManager.config.moveTimeout
            && (fightBoard?.getPathLength(fightBoard.enemyPos, fightBoard.playerPos) == null
                    || fightBoard.startCells.isEmpty())
        ) {
            sleep(500)
            fightBoard = getFightBoard()
        }
        fightBoard ?: error("Couldn't analyze fight board")

        val passTurnBounds = imgBounds("fight/ready.png") ?: error("Could not find ready button")

        val dFromEnemyToPlayer = fightBoard.getPathLength(fightBoard.playerPos, fightBoard.enemyPos) ?: Int.MAX_VALUE
        fightBoard.startCells.map { it to (fightBoard.getPathLength(it, fightBoard.enemyPos) ?: Int.MAX_VALUE) }
            .minBy { it.second }
            ?.takeIf { dFromEnemyToPlayer > it.second }
            ?.let {
                clickPoint(it.first.getCenter())
                fightBoard.playerPos = it.first
            }

        val fightAI = FightAI(6, 8, fightBoard, minRange, maxRange, 1)
        RobotUtil.press(KeyEvent.VK_F1)

        sleep(2000)
        execTimeoutOpe({}, { imgFound("fight/player_turn.png", 0.9) })
        while (!imgFound("fight/close.png") && !imgFound("fight/ok.png", 0.9)) {

            sleep(400)
            focusDofusWindow()
            sleep(400)

            if (preMoveBuffs.isNotEmpty()) {
                for (c in preMoveBuffs) {
                    RobotUtil.press(c)
                    clickPoint(fightBoard.playerPos.getCenter())
                    sleep(800)
                }
                sleep(1500)
            }
            refreshBoard(fightBoard)

            if (fightBoard.getDist(fightBoard.playerPos, fightBoard.enemyPos) ?: Int.MAX_VALUE <= 1) {
                for (c in contactAttacks) {
                    RobotUtil.press(c)
                    clickPoint(fightBoard.enemyPos.getCenter())
                }
            } else {
                fightAI.selectBestDest().takeIf { it != fightBoard.playerPos }
                    ?.let {
                        clickPoint(it.getCenter())
                        fightBoard.playerPos = it
                        sleep(2000)
                    }

                if (gapCloser.isNotEmpty()) {
                    fightBoard.cellsAtRange(gapCloserMinRange, gapCloserMaxRange, fightBoard.playerPos)
                        .filter { it.fightCellType == FightCellType.ACCESSIBLE }
                        .filter { it != fightBoard.enemyPos }
                        .minBy { fightBoard.getPathLength(it, fightBoard.enemyPos) ?: Int.MAX_VALUE }
                        ?.takeIf {
                            fightBoard.getPathLength(it, fightBoard.enemyPos) ?: Int.MAX_VALUE <
                                    fightBoard.getPathLength(fightBoard.playerPos, fightBoard.enemyPos) ?: Int.MAX_VALUE
                        }
                        ?.let {
                            for (c in gapCloser) {
                                RobotUtil.press(c)
                                clickPoint(it.getCenter())
                                sleep(800)
                            }
                            fightBoard.playerPos = it
                        }
                    sleep(1500)
                }

                val dist = fightBoard.getDist(fightBoard.playerPos, fightBoard.enemyPos) ?: Int.MAX_VALUE
                val los = fightBoard.lineOfSight(fightBoard.playerPos, fightBoard.enemyPos)
                val attacks = when {
                    dist <= 1 -> contactAttacks
                    los -> losAttacks
                    else -> nonLosAttacks
                }
                for (c in attacks) {
                    RobotUtil.press(c)
                    clickPoint(fightBoard.enemyPos.getCenter())
                }
            }

            sleep(1000)

            val capture = { captureGameImage() }
            RobotUtil.press(KeyEvent.VK_F1)

            execTimeoutOpe({ }, {
                GameInfoUtil.colorCount(capture.invoke(), passTurnBounds, FightColors.enemyTurnColors) > 120
            }, 10, false)

            execTimeoutOpe({ }, {
                GameInfoUtil.colorCount(capture.invoke(), passTurnBounds, FightColors.playerTurnColors) > 120
                        || imgFound("fight/close.png", 0.9) || imgFound("fight/ok.png", 0.9)
            })
        }
        if (imgFound("fight/ok.png", 0.9)) {
            clickChain(listOf("fight/ok.png"), "fight/close.png")
        }

        click("fight/close.png")
    }

}