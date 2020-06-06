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
import fr.lewon.dofus.bot.util.fight.FightAI
import fr.lewon.dofus.bot.util.fight.FightBoard
import fr.lewon.dofus.bot.util.fight.FightCell
import fr.lewon.dofus.bot.util.fight.FightColors
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
        endCondition: () -> Boolean,
        timeOutSeconds: Int = DTBConfigManager.config.moveTimeout,
        failOnTimeout: Boolean = true
    ) {
        startOperation.invoke()
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeOutSeconds * 1000) {
            if (endCondition.invoke()) {
                return
            }
            Thread.sleep(50)
        }
        if (failOnTimeout) {
            throw Exception("Operation timeout")
        }
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
        return imgBounds(imgName)?.let { controller.captureGameImage().getSubimage(it.x, it.y, it.width, it.height) }
    }

    protected fun getSubImage(bounds: Rectangle): BufferedImage {
        val gameImage = controller.captureGameImage()
        return gameImage.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height)
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

    protected fun getHuntPanel(): BufferedImage? {
        return GameInfoUtil.getHuntPanel(controller.captureGameImage())
    }

    protected fun reachDestination(x: Int, y: Int): Pair<Int, Int> {
        if (imgFound("start_hunt_otomai.png", 0.7)) {
            log("This is an alt world hunt.")
            return zaapAndReach("special_zaap_coordinates", x, y)
        }
        val frigostTransportersDest = loadCoordinatesDir("frigost_destinations")
        for (transporter in frigostTransportersDest) {
            val dist =
                abs(transporter.second.first - x) + abs(transporter.second.second - y)
            if (dist < 4) {
                zaapAndReach("zaap_coordinates", -76, -66)
                clickChain(listOf("frigost_transporter.png"), "frigost_transporter_frame.png")
                execTimeoutOpe(
                    { click(transporter.first) },
                    { GameInfoUtil.getLocation(controller.captureGameImage()) == transporter.second })
                return ReachMapTask(controller, logItem, x, y).runAndGet()
            }
        }
        if (abs(-56 - x) + abs(-64 - y) < 10) {
            val transporter = frigostTransportersDest.first { it.second == Pair(-56, -74) }
            zaapAndReach("zaap_coordinates", -76, -66)
            clickChain(listOf("frigost_transporter.png"), "frigost_transporter_frame.png")
            execTimeoutOpe(
                { click(transporter.first) },
                { GameInfoUtil.getLocation(controller.captureGameImage()) == transporter.second })
            return ReachMapTask(controller, logItem, x, y).runAndGet()
        }
        val otomaiTransporters = loadCoordinatesDir("otomai_destinations")
        for (transporter in otomaiTransporters) {
            val dist =
                abs(transporter.second.first - x) + abs(transporter.second.second - y)
            if (dist < 6) {
                zaapAndReach("zaap_coordinates", -49, 14)
                clickChain(listOf("otomai_transporter.png"), "otomai_transporter_frame.png")
                execTimeoutOpe(
                    { click(transporter.first) },
                    { GameInfoUtil.getLocation(controller.captureGameImage()) == transporter.second })
                return ReachMapTask(controller, logItem, x, y).runAndGet()
            }
        }
        return zaapAndReach("zaap_coordinates", x, y)
    }

    private fun zaapAndReach(zaapDir: String, x: Int, y: Int, maxDist: Int = 20): Pair<Int, Int> {
        zaapToward(zaapDir, x, y, maxDist)
        if (getLocation() == Pair(x, y)) {
            return Pair(x, y)
        }
        return ReachMapTask(controller, logItem, x, y).runAndGet()
    }

    private fun zaapToward(zaapDir: String, x: Int, y: Int, maxDist: Int = 20) {
        val zaapCoordinates = loadCoordinatesDir(zaapDir)
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
            execTimeoutOpe({ pressShortcut('h') }, { imgFound("zaap_template.png") })
        }
        clickChain(listOf("zaap_template.png"), "zaap_frame_bot.png")

        val zaapContentBounds = getFrameBounds(
            "zaap_frame_top.png",
            "zaap_frame_bot.png",
            "zaap_frame_left.png",
            "zaap_frame_right.png"
        ) ?: throw Exception("No zaap content found")


        if (!isZaapPossible(selectedCoordinate)) {
            clickPoint(
                zaapContentBounds.x + zaapContentBounds.width / 2,
                zaapContentBounds.y + zaapContentBounds.height / 2
            )
            sleep(150)
            var cpt = 0
            while (!isZaapPossible(selectedCoordinate)) {
                if (cpt++ >= 15) {
                    throw Exception("Destination not found")
                }
                mouseMove(
                    zaapContentBounds.x + zaapContentBounds.width / 2,
                    zaapContentBounds.y + zaapContentBounds.height / 2
                )
                RobotUtil.scroll(1)
                sleep(150)
            }
        }
        execTimeoutOpe(
            { clickChain(listOf(selectedCoordinate.first, "teleport_template.png")) },
            { GameInfoUtil.getLocation(controller.captureGameImage()) == selectedCoordinate.second })
    }

    private fun isZaapPossible(selectedCoordinate: Pair<String, Pair<Int, Int>>): Boolean {
        return getSubImage(selectedCoordinate.first)
            ?.let { resize(it, 16) }
            ?.let { keepWhite(it, true) }
            ?.let { getLines(it, " ()-,0123456789") }
            ?.takeIf { it.isNotEmpty() }
            ?.let { it[0].replace(" ", "") }
            ?.takeIf {
                Regex("${selectedCoordinate.second.first}.${selectedCoordinate.second.second}").find(it) != null
            } != null
    }

    protected fun pressShortcut(c: Char) {
        ClickPointTask(controller, logItem, 131, 85).runAndGet()
        RobotUtil.press(c)
    }

    protected fun getTime(): Long {
        return System.currentTimeMillis()
    }

    protected fun fight(
        minRange: Int,
        maxRange: Int,
        preMove: String,
        losAttacks: String,
        nonLosAttacks: String = "",
        aiDepth: Int = 1
    ) {
        if (imgFound("fight/creature_mode.png", 0.9)) {
            click("fight/creature_mode.png")
        }
        if (imgFound("fight/block_help.png", 0.9)) {
            click("fight/block_help.png")
        }
        var fightBoard: FightBoard? = null
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < DTBConfigManager.config.moveTimeout &&
            fightBoard?.getPathLength(fightBoard.enemyPos, fightBoard.playerPos) == null
        ) {
            sleep(500)
            try {
                fightBoard = getFightBoard()
            } catch (e: Exception) {
            }
        }
        fightBoard ?: error("Couldn't analyze fight board")

        val passTurnBounds = imgBounds("fight/ready.png") ?: error("Could not find ready button")

        var closestStart: FightCell? = null
        var minDist = fightBoard.getPathLength(fightBoard.playerPos, fightBoard.enemyPos) ?: Int.MAX_VALUE
        for (cell in fightBoard.startCells) {
            val dist = fightBoard.getPathLength(cell, fightBoard.enemyPos) ?: Int.MAX_VALUE
            if (dist < minDist) {
                minDist = dist
                closestStart = cell
            }
        }
        println(minDist)

        if (closestStart != null) {
            clickPoint(closestStart.getCenter())
            fightBoard.playerPos = closestStart
        }

        val fightAI = FightAI(6, 8, fightBoard, minRange, maxRange, aiDepth)
        RobotUtil.press(KeyEvent.VK_F1)

        sleep(2000)
        execTimeoutOpe({}, { imgFound("fight/player_turn.png", 0.9) })
        while (!imgFound("fight/close.png") && !imgFound("fight/ok.png", 0.9)) {

            sleep(800)
            if (preMove.isNotEmpty()) {
                for (c in preMove) {
                    RobotUtil.press(c)
                    clickPoint(fightBoard.playerPos.getCenter())
                    sleep(800)
                }
                sleep(1500)
            }
            refreshBoard(fightBoard)

            fightAI.selectBestDest().takeIf { it != fightBoard.playerPos }
                ?.let {
                    clickPoint(it.getCenter())
                    fightBoard.playerPos = it
                    sleep(2000)
                }

            val los = fightBoard.lineOfSight(fightBoard.playerPos, fightBoard.enemyPos)
            val attacks = if (los) losAttacks else nonLosAttacks
            for (c in attacks) {
                RobotUtil.press(c)
                clickPoint(fightBoard.enemyPos.getCenter())
            }

            sleep(1000)

            val capture = { controller.captureGameImage() }
            RobotUtil.press(KeyEvent.VK_F1)

            execTimeoutOpe({ }, {
                GameInfoUtil.colorCount(capture.invoke(), passTurnBounds, FightColors.enemyTurnColors) > 60
            }, 10, false)

            execTimeoutOpe({ }, {
                GameInfoUtil.colorCount(capture.invoke(), passTurnBounds, FightColors.playerTurnColors) > 60
                        || imgFound("fight/close.png") || imgFound("fight/ok.png")
            })
        }
        if (imgFound("fight/ok.png", 0.9)) {
            clickChain(listOf("fight/ok.png"), "fight/close.png")
        }

        click("fight/close.png")
    }

    private fun getMovePoints(fightBoard: FightBoard, characterCell: FightCell): Int {
        val characterScreenPos = characterCell.getCenter()
        execTimeoutOpe(
            { mouseMove(characterScreenPos.first, characterScreenPos.second) },
            { imgFound("fight/mp.png") }
        )
        return getSubImage("fight/mp.png")
            ?.let { it.getSubimage(27, 0, it.width - 27, it.height) }
            ?.let { resize(it, 16) }
            ?.let { keepWhite(it, true) }
            ?.let { getLines(it, "0123456789") }
            ?.takeIf { it.size == 1 }
            ?.get(0)
            ?.toInt()
            ?: 6
    }

    protected fun clearCache() {
        MatFlusher.releaseAll()
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
        val altWorld = imgFound("start_hunt_otomai.png", 0.7)
        ExecuteHuntTask(controller, logItem, controller.hintsIdsByName, altWorld).runAndGet()
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