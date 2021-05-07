package fr.lewon.dofus.bot.scripts

import fr.lewon.dofus.bot.scripts.tasks.impl.ClickButtonTask
import fr.lewon.dofus.bot.scripts.tasks.impl.ClickPointTask
import fr.lewon.dofus.bot.scripts.tasks.impl.RetrieveNextDirTask
import fr.lewon.dofus.bot.scripts.tasks.impl.complex.ExecuteHuntTask
import fr.lewon.dofus.bot.scripts.tasks.impl.complex.ReachMapTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MoveBottomTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MoveLeftTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MoveRightTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MoveTopTask
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.*
import fr.lewon.dofus.bot.util.fight.FightBoard
import fr.lewon.dofus.bot.util.fight.FightCell
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import kotlin.math.abs

abstract class DofusBotScript(val name: String) {

    private lateinit var controller: DofusTreasureBotGUIController
    private var logItem: LogItem? = null

    abstract fun getParameters(): List<DofusBotScriptParameter>

    abstract fun getStats(): List<Pair<String, String>>

    abstract fun getDescription(): String

    @Synchronized
    fun execute(controller: DofusTreasureBotGUIController, logItem: LogItem? = null) {
        this.controller = controller
        this.logItem = logItem
        val parametersByName = controller.getUser().scriptParameters[name]
            ?.map { it.key to it }
            ?.toMap()
            ?: emptyMap()
        doExecute(controller, logItem, parametersByName)
    }

    protected abstract fun doExecute(
        controller: DofusTreasureBotGUIController,
        logItem: LogItem?,
        parameters: Map<String, DofusBotScriptParameter>
    )

    protected fun runScript(script: DofusBotScript) {
        script.execute(controller, logItem)
    }

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

    protected fun getSubImage(imgName: String, minSize: Double = 1.0, maxSize: Double = 1.0): BufferedImage? {
        return imgBounds(imgName, minSize, maxSize)?.let {
            controller.captureGameImage().getSubimage(it.x, it.y, it.width, it.height)
        }
    }

    protected fun getSubImage(bounds: Rectangle): BufferedImage {
        val gameImage = controller.captureGameImage()
        return gameImage.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height)
    }

    protected fun clickPoint(point: Pair<Int, Int>) {
        clickPoint(point.first, point.second)
    }

    protected fun clickPoint(x: Int, y: Int) {
        ClickPointTask(controller, logItem, x, y).run()
    }

    protected fun doubleClickPoint(point: Pair<Int, Int>) {
        doubleClickPoint(point.first, point.second)
    }

    protected fun doubleClickPoint(x: Int, y: Int) {
        ClickPointTask(controller, logItem, x, y, 2).run()
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

    protected fun click(imgName: String, li: LogItem? = logItem) {
        ClickButtonTask(
            controller,
            li,
            "scripts_templates/$imgName"
        ).run()
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
                return ReachMapTask(
                    controller,
                    logItem,
                    x,
                    y
                ).run()
            }
        }
        if (abs(-56 - x) + abs(-64 - y) < 10) {
            val transporter = frigostTransportersDest.first { it.second == Pair(-56, -74) }
            zaapAndReach("zaap_coordinates", -76, -66)
            clickChain(listOf("frigost_transporter.png"), "frigost_transporter_frame.png")
            execTimeoutOpe(
                { click(transporter.first) },
                { GameInfoUtil.getLocation(controller.captureGameImage()) == transporter.second })
            return ReachMapTask(
                controller,
                logItem,
                x,
                y
            ).run()
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
                return ReachMapTask(
                    controller,
                    logItem,
                    x,
                    y
                ).run()
            }
        }
        return zaapAndReach("zaap_coordinates", x, y)
    }

    private fun zaapAndReach(zaapDir: String, x: Int, y: Int, maxDist: Int = 20): Pair<Int, Int> {
        zaapToward(zaapDir, x, y, maxDist)
        if (getLocation() == Pair(x, y)) {
            return Pair(x, y)
        }
        return ReachMapTask(controller, logItem, x, y)
            .run()
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

        reachHome()
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

    private fun reachHome() {
        if (imgFound("zaap_template.png", 0.90)) {
            return
        }
        while (true) {
            try {
                execTimeoutOpe({ pressShortcut('h') }, { imgFound("zaap_template.png", 0.90) })
                return
            } catch (e: Exception) {
                if (!MovesHistory.cancelLastMove(controller, logItem)) {
                    error("Couldn't reach home - ${e.localizedMessage}")
                }
            }
        }
    }

    private fun isZaapPossible(selectedCoordinate: Pair<String, Pair<Int, Int>>): Boolean {
        return getSubImage(selectedCoordinate.first, 1.0, 1.6)
            ?.let { resize(it, 16) }
            ?.let { keepWhite(it, true) }
            ?.let { getLines(it, " ()-,0123456789") }
            ?.takeIf { it.isNotEmpty() }
            ?.let { it[0].replace(" ", "") }
            ?.takeIf {
                Regex("${selectedCoordinate.second.first}.${selectedCoordinate.second.second}").find(it) != null
            } != null
    }

    protected fun openMenu() {
        focusDofusWindow()
        RobotUtil.escape()
    }

    protected fun pressShortcut(c: Char) {
        focusDofusWindow()
        RobotUtil.press(c)
    }

    protected fun focusDofusWindow() {
        ClickPointTask(controller, logItem, 131, 85).run()
    }

    protected fun getTime(): Long {
        return System.currentTimeMillis()
    }

    protected fun captureGameImage(): BufferedImage {
        return controller.captureGameImage()
    }

    private fun getMovePoints(characterCell: FightCell): Int {
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

    protected fun clearMatCache() {
        MatFlusher.releaseAll()
    }

    protected fun getFightBoard(): FightBoard? {
        return try {
            GameInfoUtil.getFightBoard(controller.captureGameImage())
        } catch (e: Exception) {
            null
        }
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
        return OCRUtil.getAllLines(
            getSubImage(imgName) ?: return emptyList(), charWhitelist
        )
    }

    protected fun getLines(image: BufferedImage, charWhitelist: String? = null): List<String> {
        return OCRUtil.getAllLines(image, charWhitelist)
    }

    protected fun keepDark(image: BufferedImage, smoothen: Boolean = true): BufferedImage {
        return OCRUtil.keepDarkOnImage(
            ImageUtil.bufferedImageToMat(
                image
            ), smoothen
        )
    }

    protected fun keepWhite(image: BufferedImage, smoothen: Boolean = true): BufferedImage {
        return OCRUtil.keepWhiteOnImage(
            ImageUtil.bufferedImageToMat(
                image
            ), smoothen
        )
    }

    protected fun log(message: String, li: LogItem? = logItem) {
        controller.log(message, li)
    }

    protected fun getNextDir(): String {
        return RetrieveNextDirTask(controller, logItem).run().name
    }

    protected fun getLocation(): Pair<Int, Int> {
        return GameInfoUtil.getLocation(controller.captureGameImage())
            ?: throw Exception("Couldn't retrieve current location")
    }

    protected fun imgFound(imgName: String, minMatchValue: Double = 0.6): Boolean {
        return imgCenter(imgName, minMatchValue) != null
    }

    protected fun imgBounds(imgName: String, minSize: Double = 1.0, maxSize: Double = 1.0): Rectangle? {
        return GameInfoUtil.getButtonBounds(
            controller.captureGameImage(),
            "scripts_templates/$imgName",
            minSize = minSize,
            maxSize = maxSize
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
        val huntLvlTemplatePath = "../${controller.getHuntLvlTemplatePath()}"
        execTimeoutOpe(
            {
                clickPoint(1043, 487)
                sleep(2500)
            },
            { imgFound(huntLvlTemplatePath, 0.9) }
        )
        clickChain(
            listOf(huntLvlTemplatePath), "../templates/hunt_frame_top.png"
        )
    }

    protected fun executeHunt() {
        val altWorld = imgFound("start_hunt_otomai.png", 0.7)
        ExecuteHuntTask(
            controller,
            logItem,
            controller.hintsIdsByName,
            altWorld
        ).run()
    }

    protected fun moveLeft(count: Int = 1): Pair<Int, Int> {
        for (i in 1 until count) {
            MoveLeftTask(controller, logItem).run()
        }
        return MoveLeftTask(controller, logItem).run()
    }

    protected fun moveRight(count: Int = 1): Pair<Int, Int> {
        for (i in 1 until count) {
            MoveRightTask(controller, logItem).run()
        }
        return MoveRightTask(controller, logItem).run()
    }

    protected fun moveTop(count: Int = 1): Pair<Int, Int> {
        for (i in 1 until count) {
            MoveTopTask(controller, logItem).run()
        }
        return MoveTopTask(controller, logItem).run()
    }

    protected fun moveBottom(count: Int = 1): Pair<Int, Int> {
        for (i in 1 until count) {
            MoveBottomTask(controller, logItem).run()
        }
        return MoveBottomTask(controller, logItem).run()
    }

    override fun toString(): String {
        return name
    }
}