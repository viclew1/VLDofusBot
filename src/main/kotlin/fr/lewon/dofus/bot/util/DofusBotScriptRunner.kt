package fr.lewon.dofus.bot.util

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.ui.logic.tasks.ClickButtonTask
import fr.lewon.dofus.bot.ui.logic.tasks.ClickPointTask
import fr.lewon.dofus.bot.ui.logic.tasks.RetrieveNextDirTask
import fr.lewon.dofus.bot.ui.logic.tasks.complex.ExecuteHuntTask
import fr.lewon.dofus.bot.ui.logic.tasks.complex.ReachMapTask
import fr.lewon.dofus.bot.ui.logic.tasks.moves.MoveBottomTask
import fr.lewon.dofus.bot.ui.logic.tasks.moves.MoveLeftTask
import fr.lewon.dofus.bot.ui.logic.tasks.moves.MoveRightTask
import fr.lewon.dofus.bot.ui.logic.tasks.moves.MoveTopTask
import fr.lewon.dofus.bot.util.fight.FightBoard
import java.awt.Rectangle
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.script.ScriptEngineManager
import kotlin.math.abs

object DofusBotScriptRunner {

    @Synchronized
    fun runScript(controller: DofusTreasureBotGUIController, logItem: LogItem?, script: File, vararg args: Any) {
        val scriptLogItem = controller.log("Starting script [${script.name}] ... ", logItem)
        try {
            with(ScriptEngineManager().getEngineByExtension("kts")) {
                put("c", controller)
                put("l", scriptLogItem)
                val dofusBotScriptStr = """
                    object : ${DofusBotScript::class.qualifiedName} (
                        bindings["c"] as ${DofusTreasureBotGUIController::class.qualifiedName}, 
                        bindings["l"] as ${LogItem::class.qualifiedName}?
                    ) {
                        override fun execute(vararg args: Any) {
                            ${script.readText()}
                        }
                    }
                """
                val parsedScript = eval(dofusBotScriptStr) as DofusBotScript
                parsedScript.execute(args)
            }
            controller.closeLog("OK", scriptLogItem)
        } catch (e: Exception) {
            controller.closeLog("KO - ${e.localizedMessage}", scriptLogItem)
            throw e
        }
    }

}

abstract class DofusBotScript(
    private val controller: DofusTreasureBotGUIController,
    private val logItem: LogItem? = null
) {

    abstract fun execute(vararg args: Any)

    protected fun execTimeoutOpe(
        startOperation: () -> Unit,
        endCondition: () -> Boolean
    ) {
        startOperation.invoke()
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < DTBConfigManager.config.moveTimeout * 1000) {
            if (endCondition.invoke()) {
                return
            }
            Thread.sleep(50)
        }
        throw Exception("Operation timeout")
    }

    protected fun sleep(millis: Long) {
        Thread.sleep(millis)
    }

    protected fun phorrorHere(): Boolean {
        return GameInfoUtil.phorrorOnMap(controller.captureGameImage())
    }

    protected fun patternFound(imgNames: List<String>, minMatchValue: Double = 0.21): Boolean {
        return GameInfoUtil.patternFound(
            gameImage = controller.captureGameImage(),
            minMatchValue = minMatchValue,
            templatesPath = *imgNames.map { "scripts_templates/$it" }.toTypedArray()
        )
    }

    protected fun runScript(scriptName: String, vararg args: Any) {
        DofusBotScriptRunner.runScript(controller, logItem, File("scripts/$scriptName"), args)
    }

    protected fun getFrameContent(
        topFrameImgName: String,
        botFrameImgName: String,
        leftFrameImgName: String,
        rightFrameImgName: String
    ): BufferedImage? {
        return GameInfoUtil.getFrameContent(
            gameImage = controller.captureGameImage(),
            topFramePath = "scripts_templates/$topFrameImgName",
            botFramePath = "scripts_templates/$botFrameImgName",
            leftFramePath = "scripts_templates/$leftFrameImgName",
            rightFramePath = "scripts_templates/$rightFrameImgName"
        )
    }

    protected fun getFrameBounds(
        topFrameImgName: String,
        botFrameImgName: String,
        leftFrameImgName: String,
        rightFrameImgName: String
    ): Rectangle? {
        return GameInfoUtil.getFrameBounds(
            gameImage = controller.captureGameImage(),
            topFramePath = "scripts_templates/$topFrameImgName",
            botFramePath = "scripts_templates/$botFrameImgName",
            leftFramePath = "scripts_templates/$leftFrameImgName",
            rightFramePath = "scripts_templates/$rightFrameImgName"
        )
    }

    protected fun getSubImage(imgName: String): BufferedImage? {
        val gameImage = controller.captureGameImage()
        return imgBounds(imgName)?.let { gameImage.getSubimage(it.x, it.y, it.width, it.height) }
    }

    protected fun clickPoint(point: Pair<Int, Int>) {
        clickPoint(point.first, point.second)
    }

    protected fun clickPoint(x: Int, y: Int) {
        ClickPointTask(controller, logItem, x, y).runAndGet()
    }

    protected fun clickChain(
        imgNames: List<String>,
        expectedFinalImageName: String? = null
    ) {
        val it = imgNames.iterator()
        var currentImg = it.next()
        while (it.hasNext()) {
            val nextImg = it.next()
            execTimeoutOpe({ click(currentImg) }, { imgFound(nextImg) })
            currentImg = nextImg
        }

        expectedFinalImageName?.let {
            execTimeoutOpe(
                { click(imgNames.last()) },
                { imgFound(expectedFinalImageName) }
            )
        } ?: click(imgNames.last())
    }

    protected fun click(imgName: String) {
        ClickButtonTask(controller, logItem, "scripts_templates/$imgName").runAndGet()
    }

    protected fun mouseMove(x: Int, y: Int) {
        val screenBounds = controller.getGameScreen().defaultConfiguration.bounds
        RobotUtil.move(screenBounds.x + x, screenBounds.y + y)
    }

    private fun loadCoordinatesDir(dirName: String): List<Pair<String, Pair<Int, Int>>> {
        return File("scripts_templates/$dirName").listFiles()
            ?.map {
                "$dirName/${it.name}" to
                        Regex("(-?[0-9]+[\b]*_[\b]*-?[0-9]+)\\.png").find(it.name)
                            ?.destructured
                            ?.component1()
            }
            ?.filter { it.second != null }
            ?.map {
                it.first to
                        Pair(
                            it.second!!.split("_")[0].toInt(),
                            it.second!!.split("_")[1].toInt()
                        )
            }
            ?: throw Exception("No coordinates found in dir [$dirName]")
    }


    protected fun reachDestination(x: Int, y: Int): Pair<Int, Int> {
        val frigostTransportersDest = loadCoordinatesDir("frigost_destinations")
        for (transporter in frigostTransportersDest) {
            val dist =
                abs(transporter.second.first - x) + abs(transporter.second.second - y)
            if (dist < 4) {
                zaapAndReach(-76, -66)
                clickChain(listOf("frigost_transporter.png"), "frigost_transporter_frame.png")
                execTimeoutOpe(
                    { click(transporter.first) },
                    { GameInfoUtil.getLocation(controller.captureGameImage()) == transporter.second })
                return ReachMapTask(controller, logItem, x, y).runAndGet()
            }
        }
        val otomaiTransporters = loadCoordinatesDir("otomai_destinations")
        for (transporter in otomaiTransporters) {
            val dist =
                abs(transporter.second.first - x) + abs(transporter.second.second - y)
            if (dist < 6) {
                zaapAndReach(-49, 14)
                clickChain(listOf("otomai_transporter.png"), "otomai_transporter_frame.png")
                execTimeoutOpe(
                    { click(transporter.first) },
                    { GameInfoUtil.getLocation(controller.captureGameImage()) == transporter.second })
                return ReachMapTask(controller, logItem, x, y).runAndGet()
            }
        }
        return zaapAndReach(x, y)
    }

    private fun zaapAndReach(x: Int, y: Int, maxDist: Int = 20): Pair<Int, Int> {
        zaapToward(x, y, maxDist)
        return ReachMapTask(controller, logItem, x, y).runAndGet()
    }

    private fun zaapToward(x: Int, y: Int, maxDist: Int = 20) {
        val zaapCoordinates = loadCoordinatesDir("zaap_coordinates")
        var selectedCoordinate: Pair<String, Pair<Int, Int>>? = null
        var closestDir = Int.MAX_VALUE
        for (coordinate in zaapCoordinates) {
            val dir = abs(coordinate.second.first - x) + abs(coordinate.second.second - y)
            if (dir < closestDir) {
                closestDir = dir
                selectedCoordinate = coordinate
            }
        }

        if (selectedCoordinate == null || closestDir > maxDist) {
            throw Exception("No zaap found near destination")
        }

        log("Moving to zaap [${selectedCoordinate.second.first},${selectedCoordinate.second.second}]")

        if (getLocation() == selectedCoordinate.second) {
            return
        }

        if (!imgFound("zaap_template.png")) {
            clickChain(listOf("home_template.png"), "zaap_template.png")
        }
        clickChain(listOf("zaap_template.png"), "zaap_frame_bot.png")

        val zaapContentBounds = getFrameBounds(
            "zaap_frame_top.png",
            "zaap_frame_bot.png",
            "zaap_frame_left.png",
            "zaap_frame_right.png"
        ) ?: throw Exception("No zaap content found")

        var cpt = 0
        while (cpt++ < 15) {
            clickPoint(
                zaapContentBounds.x + zaapContentBounds.width / 2,
                zaapContentBounds.y + zaapContentBounds.height / 2
            )
            mouseMove(
                zaapContentBounds.x + zaapContentBounds.width / 2,
                zaapContentBounds.y + zaapContentBounds.height / 2
            )
            sleep(150)
            getSubImage(selectedCoordinate.first)
                ?.let { resize(it, 16) }
                ?.let { keepWhite(it, true) }
                ?.let { getLines(it, " ()-,0123456789") }
                ?.takeIf { it.isNotEmpty() }
                ?.let { it[0].replace(" ", "") }
                ?.takeIf {
                    Regex("${selectedCoordinate.second.first}.${selectedCoordinate.second.second}").find(it) != null
                }
                ?.let {
                    execTimeoutOpe(
                        { clickChain(listOf(selectedCoordinate.first, "teleport_template.png")) },
                        { GameInfoUtil.getLocation(controller.captureGameImage()) == selectedCoordinate.second })
                    return
                }
            RobotUtil.scroll(1)
        }
        throw Exception("Destination not found")
    }

    fun fight(preferredRange: Int, preMove: String, attacks: String) {
        val fightBoard = getFightBoard()
        var closestStart: fr.lewon.dofus.bot.util.fight.FightCell? = null
        var minDist = fightBoard.getDist(fightBoard.yourPos, fightBoard.enemyPos) ?: Int.MAX_VALUE
        for (cell in fightBoard.startCells) {
            val dist = fightBoard.getDist(cell, fightBoard.enemyPos) ?: Int.MAX_VALUE
            println(dist)
            if (dist < minDist) {
                minDist = dist
                closestStart = cell
            }
        }
        println(minDist)

        if (closestStart != null && imgFound("fight/ready.png")) {
            clickPoint(closestStart.getCenter())
            fightBoard.yourPos = closestStart
        }

        RobotUtil.press(KeyEvent.VK_F1)

        execTimeoutOpe({}, { imgFound("fight/finish_turn.png", 0.9) })

        while (!imgFound("fight/close.png")) {
            sleep(2000)
            if (preMove.isNotEmpty()) {
                for (c in preMove) {
                    RobotUtil.press(c)
                    clickPoint(fightBoard.yourPos.getCenter())
                    sleep(350)
                }
                sleep(1500)
            }
            refreshBoard(fightBoard)

            val accessibleCells = fightBoard.accessibleCells
            val idealCells = fightBoard.cellsAtRange(preferredRange, fightBoard.enemyPos)
                .toMutableList()
            idealCells.retainAll(accessibleCells)

            if (idealCells.isNotEmpty()) {
                clickPoint(idealCells[0].getCenter())
                fightBoard.yourPos = idealCells[0]
            } else if (accessibleCells.isNotEmpty()) {
                var closest = accessibleCells[0]
                var minD = Int.MAX_VALUE
                for (cell in accessibleCells) {
                    val dist = fightBoard.getDist(cell, fightBoard.enemyPos) ?: Int.MAX_VALUE
                    if (dist < minD) {
                        minD = dist
                        closest = cell
                    }
                }
                clickPoint(closest.getCenter())
                fightBoard.yourPos = closest
            }

            sleep(2000)
            for (c in attacks) {
                RobotUtil.press(c)
                clickPoint(fightBoard.enemyPos.getCenter())
            }

            sleep(500)
            RobotUtil.press(KeyEvent.VK_F1)
            execTimeoutOpe({ sleep(5000) }, { imgFound("fight/finish_turn.png", 0.85) || imgFound("fight/close.png") })
        }
        click("fight/close.png")
    }

    protected fun getFightBoard(): FightBoard {
        return GameInfoUtil.getFightBoard(controller.captureGameImage())
    }

    protected fun refreshBoard(fightBoard: FightBoard) {
        GameInfoUtil.refreshBoard(fightBoard, controller.captureGameImage())
    }

    protected fun locatePoint() {
        controller.registerPos(logItem)
    }

    protected fun resize(image: BufferedImage, ratio: Int): BufferedImage {
        return ImageUtil.resizeImage(image, ratio)
    }

    protected fun getLines(imgName: String, charWhitelist: String? = null): List<String> {
        return OCRUtil.getAllLines(getSubImage(imgName) ?: return emptyList(), charWhitelist)
    }

    protected fun getLines(image: BufferedImage, charWhitelist: String? = null): List<String> {
        return OCRUtil.getAllLines(image, charWhitelist)
    }

    protected fun keepDark(image: BufferedImage, smoothen: Boolean = true): BufferedImage {
        return OCRUtil.keepDarkOnImage(ImageUtil.bufferedImageToMat(image), smoothen)
    }

    protected fun keepWhite(image: BufferedImage, smoothen: Boolean = true): BufferedImage {
        return OCRUtil.keepWhiteOnImage(ImageUtil.bufferedImageToMat(image), smoothen)
    }

    protected fun log(message: String) {
        controller.log(message, logItem)
    }

    protected fun getNextDir(): String {
        return RetrieveNextDirTask(controller, logItem).runAndGet().name
    }

    protected fun getLocation(): Pair<Int, Int> {
        return GameInfoUtil.getLocation(controller.captureGameImage())
            ?: throw Exception("Couldn't retrieve current location")
    }

    protected fun imgFound(imgName: String, minMatchValue: Double = 0.6): Boolean {
        return imgCenter(imgName, minMatchValue) != null
    }

    protected fun imgBounds(imgName: String): Rectangle? {
        return GameInfoUtil.getButtonBounds(
            controller.captureGameImage(),
            "scripts_templates/$imgName"
        )
    }

    protected fun imgCenter(imgName: String, minMatchValue: Double = 0.6): Pair<Int, Int>? {
        return GameInfoUtil.getButtonCenter(
            controller.captureGameImage(),
            "scripts_templates/$imgName",
            minMatchValue
        )
    }

    protected fun selectHunt() {
        clickChain(
            listOf("hunt_chest.png"), "../${controller.getHuntLvlTemplatePath()}"
        )
        sleep(2500)
        clickChain(
            listOf("../${controller.getHuntLvlTemplatePath()}"), "../templates/hunt_frame_top.png"
        )
    }

    protected fun executeHunt() {
        ExecuteHuntTask(controller, logItem, controller.hintsIdsByName).runAndGet()
    }

    protected fun moveLeft(count: Int = 1): Pair<Int, Int> {
        for (i in 1 until count) {
            MoveLeftTask(controller, logItem).runAndGet()
        }
        return MoveLeftTask(controller, logItem).runAndGet()
    }

    protected fun moveRight(count: Int = 1): Pair<Int, Int> {
        for (i in 1 until count) {
            MoveRightTask(controller, logItem).runAndGet()
        }
        return MoveRightTask(controller, logItem).runAndGet()
    }

    protected fun moveTop(count: Int = 1): Pair<Int, Int> {
        for (i in 1 until count) {
            MoveTopTask(controller, logItem).runAndGet()
        }
        return MoveTopTask(controller, logItem).runAndGet()
    }

    protected fun moveBottom(count: Int = 1): Pair<Int, Int> {
        for (i in 1 until count) {
            MoveBottomTask(controller, logItem).runAndGet()
        }
        return MoveBottomTask(controller, logItem).runAndGet()
    }

}