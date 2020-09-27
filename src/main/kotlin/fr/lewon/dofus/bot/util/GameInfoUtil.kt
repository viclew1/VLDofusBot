package fr.lewon.dofus.bot.util

import fr.lewon.dofus.bot.util.ImageUtil.bufferedImageToMat
import fr.lewon.dofus.bot.util.ImageUtil.resizeImage
import fr.lewon.dofus.bot.util.fight.FightBoard
import fr.lewon.dofus.bot.util.fight.FightCell
import fr.lewon.dofus.bot.util.fight.FightCellType
import fr.lewon.dofus.bot.util.fight.FightColors
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.Color
import java.awt.Rectangle
import java.awt.image.BufferedImage
import kotlin.math.abs


object GameInfoUtil {

    private fun buildMat(imgPath: String): Mat {
        return Imgcodecs.imread(imgPath)
    }

    @Synchronized
    fun getButtonBounds(gameImage: BufferedImage, imagePath: String, minMatchValue: Double = 0.6): Rectangle? {
        val searchTemplate = buildMat(imagePath)
        val matchResult = this.getMatchResult(gameImage, searchTemplate) ?: return null
        if (matchResult.maxVal < minMatchValue) return null
        return Rectangle(
            matchResult.maxLoc.x.toInt(),
            matchResult.maxLoc.y.toInt(),
            searchTemplate.cols(),
            searchTemplate.rows()
        )
    }

    @Synchronized
    fun getButtonCenter(gameImage: BufferedImage, imagePath: String, minMatchValue: Double = 0.6): Pair<Int, Int>? {
        return getButtonBounds(gameImage, imagePath, minMatchValue)
            ?.let { Pair(it.centerX.toInt(), it.centerY.toInt()) }
            ?: return null
    }

    @Synchronized
    fun getHuntObjectives(huntContent: BufferedImage): List<String> {
        return OCRUtil.getAllLines(
            OCRUtil.keepDarkOnImage(
                bufferedImageToMat(resizeImage(huntContent, 6))
            )
        ).filter { it.length > 2 }
    }

    @Synchronized
    fun getNextDirection(gameImage: BufferedImage): Directions? {
        // Building templates for the sides of the frame, because its size may vary
        val tempTopMat = buildMat(DofusImages.TREASURE_HUNT_FRAME_TOP_TEMPLATE.path)
        val tempBotMat = buildMat(DofusImages.TREASURE_HUNT_FRAME_BOT_TEMPLATE.path)
        val tempLeftMat = buildMat(DofusImages.TREASURE_HUNT_FRAME_LEFT_TEMPLATE.path)

        // Fetching the match on the game capture of the templates build before, fails if the match value is less than 0.25
        val topMatchResult = this.getMatchResult(gameImage, tempTopMat) ?: return null
        if (topMatchResult.maxVal < 0.25) return null
        val botMatchResult = this.getMatchResult(gameImage, tempBotMat) ?: return null
        if (botMatchResult.maxVal < 0.25) return null
        val leftMatchResult = this.getMatchResult(gameImage, tempLeftMat) ?: return null
        if (leftMatchResult.maxVal < 0.25) return null

        // Keeping only the arrows part of the hunt content
        val x = leftMatchResult.maxLoc.x.toInt()
        val y = topMatchResult.maxLoc.y.toInt() + tempTopMat.rows()
        val w = tempLeftMat.cols()
        val h = botMatchResult.maxLoc.y.toInt() - y

        if (w <= 0 || h <= 0) {
            return null
        }

        val subImage = OCRUtil.keepDarkOnImage(
            bufferedImageToMat(gameImage.getSubimage(x, y, w, h)), false
        )

        val lastArrow = OCRUtil.splitByLine(subImage, 1).last()

        val blackColor = listOf(Color(0, 0, 0).rgb)
        if (lastArrow.width > lastArrow.height) {
            val leftHalf = Rectangle(0, 0, lastArrow.width / 2, lastArrow.height)
            val rightHalf = Rectangle(lastArrow.width / 2, 0, lastArrow.width / 2, lastArrow.height)
            if (colorCount(lastArrow, leftHalf, blackColor) > colorCount(lastArrow, rightHalf, blackColor)) {
                return Directions.LEFT
            }
            return Directions.RIGHT
        } else {
            val topHalf = Rectangle(0, 0, lastArrow.width, lastArrow.height / 2)
            val bottomHalf = Rectangle(0, lastArrow.height / 2, lastArrow.width, lastArrow.height / 2)
            if (colorCount(lastArrow, topHalf, blackColor) > colorCount(lastArrow, bottomHalf, blackColor)) {
                return Directions.TOP
            }
            return Directions.BOTTOM
        }
    }

    /**
     * Analyzes the image to find a phorror. Returns true if a phorror is found, else false
     * @param gameImage
     */
    @Synchronized
    fun phorrorOnMap(gameImage: BufferedImage, minMatchValue: Double = 0.21, segmentImages: Boolean = true): Boolean {
        return patternFound(
            gameImage,
            minMatchValue,
            segmentImages,
            DofusImages.BL_PHORROR.path,
            DofusImages.BR_PHORROR.path,
            DofusImages.TL_PHORROR.path,
            DofusImages.TR_PHORROR.path
        )
    }

    fun refreshBoard(fightBoard: FightBoard, gameImage: BufferedImage) {
        fightBoard.accessibleCells.clear()

        val explored = mutableListOf(fightBoard.playerPos)
        var frontier = listOf(fightBoard.playerPos)
        while (frontier.isNotEmpty()) {
            val newFrontier = ArrayList<FightCell>()
            for (cell in frontier) {
                for (n in cell.neighbors) {
                    if (!explored.contains(n) && n.fightCellType == FightCellType.ACCESSIBLE) {
                        explored.add(n)
                        val bounds = n.bounds
                        val color = gameImage.getRGB(bounds.x + bounds.width / 2, bounds.y + bounds.height / 4)
                        if (FightColors.moveColors.contains(color)) {
                            fightBoard.accessibleCells.add(n)
                            newFrontier.add(n)
                        }
                    }
                }
            }
            frontier = newFrontier
        }

        findCharacterTile(gameImage, FightColors.enemyColors, fightBoard)
            ?.let { fightBoard.enemyPos = it }
//        findCharacterTile(gameImage, FightColors.playerColors, fightBoard)
//            ?.let { fightBoard.playerPos = it }
    }

    private fun findCharacterTile(gameImage: BufferedImage, colors: List<Int>, fightBoard: FightBoard): FightCell? {
        return fightBoard.cells
            .filter { it.fightCellType == FightCellType.ACCESSIBLE }
            .maxBy { colorCount(gameImage, it.bounds, colors) }
    }

    fun getFightBoard(gameImage: BufferedImage): FightBoard {
        val startTiles = ArrayList<FightCell>()
        val tiles = ArrayList<FightCell>()

        val tileWidth = (gameImage.width - 650).toDouble() / 14.5
        val tileHeight = (gameImage.height - 200).toDouble() / 20.0
        val initialX = 325 + tileWidth / 2.0
        val initialY = 23 + tileHeight / 4.0

        for (xMultiplier in 0 until 28) {
            val col = (initialX + (xMultiplier.toDouble() / 2.0) * tileWidth).toInt()
            for (yMultiplier in 0 until 20) {
                val currentTileRow = -xMultiplier / 2 + yMultiplier
                val currentTileCol = xMultiplier / 2 + xMultiplier % 2 + yMultiplier
                val row = (initialY + yMultiplier * tileHeight + (xMultiplier % 2) * tileHeight / 2.0).toInt()
                val color = gameImage.getRGB(col, row)
                val x = col.toDouble() - tileWidth / 2.0
                val y = row.toDouble() - tileHeight / 4.0
                val tileBounds = Rectangle(x.toInt(), y.toInt(), tileWidth.toInt(), tileHeight.toInt())
                val tile = when {
                    FightColors.playerStartColors.contains(color) -> {
                        val tile = FightCell(currentTileRow, currentTileCol, tileBounds, FightCellType.ACCESSIBLE)
                        startTiles.add(tile)
                        tile
                    }
                    FightColors.holeColors.contains(color) -> {
                        FightCell(currentTileRow, currentTileCol, tileBounds, FightCellType.HOLE)
                    }
                    FightColors.wallColors.contains(color) -> {
                        FightCell(currentTileRow, currentTileCol, tileBounds, FightCellType.WALL)
                    }
                    else -> {
                        FightCell(currentTileRow, currentTileCol, tileBounds, FightCellType.ACCESSIBLE)
                    }
                }
                tiles.add(tile)
            }
        }

        val playerTile = tiles.maxBy { colorCount(gameImage, it.bounds, FightColors.playerColors) }
            ?: error("Cannot find player character")
        val enemyTile = tiles.maxBy { colorCount(gameImage, it.bounds, FightColors.enemyColors) }
            ?: error("Cannot find enemy character")

        return FightBoard(tiles, startTiles, playerTile, enemyTile).also { it.init() }
    }

    fun colorCount(img: BufferedImage, bounds: Rectangle, colors: List<Int>): Int {
        var cpt = 0
        for (x in bounds.x until bounds.x + bounds.width) {
            for (y in bounds.y until bounds.y + bounds.height) {
                val color = img.getRGB(x, y)
                if (colors.contains(color)) {
                    cpt++
                }
            }
        }
        return cpt
    }

    @Synchronized
    fun patternFound(
        gameImage: BufferedImage,
        minMatchValue: Double = 0.21,
        segmentImages: Boolean = true,
        vararg templatesPath: String
    ): Boolean {
        val imgMat = OCRUtil.segmentImage(bufferedImageToMat(gameImage))
        var templates = templatesPath.map { buildMat(it) }
        if (segmentImages) {
            templates = templates.map { OCRUtil.segmentImage(it) }
        }

        for (template in templates) {
            val matchValue = getMatchResult(imgMat, template)?.maxVal ?: -1.0
            println(matchValue)
            if (matchValue > minMatchValue) {
                return true
            }
        }
        return false
    }

    /**
     * Returns a Pair<Int, Int> corresponding to the coordinates on the map, first element being "x" and the second being "y"
     * @param gameImage
     */
    @Synchronized
    fun getLocation(gameImage: BufferedImage): Pair<Int, Int>? {
        // Matching the game capture to the location template.
        val locationImage = gameImage.getSubimage(
            0,
            70,
            200,
            30
        )

        for (ratio in 3..6) {
            var treatedImage = resizeImage(locationImage, ratio)
            treatedImage = OCRUtil.keepWhiteOnImage(bufferedImageToMat(treatedImage))

            // Use Tesseract to get the lines present in the image, fails if there is more than one line
            val lines = OCRUtil.getAllLines(treatedImage)
            if (lines.isEmpty()) {
                continue
            }
            println(lines)

            val treatedLines = lines.last().replace(" ", "").replace(".", ",")

            val coordinatesStr = Regex("(-?[0-9]+[\b]*,[\b]*-?[0-9]+)").find(treatedLines)
                ?.destructured
                ?.component1()
                ?: continue

            val coordinatesStrSplit = coordinatesStr.split(",")

            // Return a pair of integers corresponding to the coordinates
            val x = coordinatesStrSplit[0].trim().toInt()
            val y = coordinatesStrSplit[1].trim().toInt()
            if (abs(x) >= 100 || abs(y) >= 100) {
                continue
            }
            return Pair(x, y)
        }
        return null
    }

    /**
     * Returns the image corresponding to the hunt content panel, removing the frame, the start indicator, the direction indicators on the left and the checkpoints buttons on the right
     * @param gameImage
     */
    @Synchronized
    fun getHuntPanel(gameImage: BufferedImage): BufferedImage? {
        return getFrameContent(
            gameImage,
            DofusImages.TREASURE_HUNT_FRAME_TOP_TEMPLATE.path,
            DofusImages.TREASURE_HUNT_FRAME_BOT_TEMPLATE.path,
            DofusImages.TREASURE_HUNT_FRAME_LEFT_TEMPLATE.path,
            DofusImages.TREASURE_HUNT_FRAME_RIGHT_TEMPLATE.path
        )
    }

    @Synchronized
    fun getFrameContent(
        gameImage: BufferedImage,
        topFramePath: String,
        botFramePath: String,
        leftFramePath: String,
        rightFramePath: String
    ): BufferedImage? {
        val bounds = getFrameBounds(gameImage, topFramePath, botFramePath, leftFramePath, rightFramePath)
            ?: return null
        val content = gameImage.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height)

        // Returns the content image with the templates removed
        return content
    }

    fun getFrameBounds(
        gameImage: BufferedImage,
        topFramePath: String,
        botFramePath: String,
        leftFramePath: String,
        rightFramePath: String
    ): Rectangle? {
        // Building templates for the sides of the frame, because its size may vary
        val tempTopMat = buildMat(topFramePath)
        val tempBotMat = buildMat(botFramePath)
        val tempLeftMat = buildMat(leftFramePath)
        val tempRightMat = buildMat(rightFramePath)

        // Fetching the match on the game capture of the templates build before, fails if the match value is less than 0.25
        val topMatchResult = this.getMatchResult(gameImage, tempTopMat) ?: return null
        if (topMatchResult.maxVal < 0.25) return null
        val botMatchResult = this.getMatchResult(gameImage, tempBotMat) ?: return null
        if (botMatchResult.maxVal < 0.25) return null
        val leftMatchResult = this.getMatchResult(gameImage, tempLeftMat) ?: return null
        if (leftMatchResult.maxVal < 0.25) return null
        val rightMatchResult = this.getMatchResult(gameImage, tempRightMat) ?: return null
        if (rightMatchResult.maxVal < 0.25) return null

        val x = leftMatchResult.maxLoc.x.toInt() + tempLeftMat.cols()
        val y = topMatchResult.maxLoc.y.toInt() + tempTopMat.rows()
        val w = rightMatchResult.maxLoc.x.toInt() - x
        val h = botMatchResult.maxLoc.y.toInt() - y

        if (w <= 0 || h <= 0) {
            return null
        }
        return Rectangle(x, y, w, h)
    }

    @Synchronized
    private fun getMatchResult(originalImage: BufferedImage, templateMat: Mat): Core.MinMaxLocResult? {
        return getMatchResult(bufferedImageToMat(originalImage), templateMat)
    }

    @Synchronized
    private fun getMatchResult(imgMat: Mat, templateMat: Mat): Core.MinMaxLocResult? {
        val outputImageHunt = Mat()
        Imgproc.matchTemplate(
            imgMat,
            templateMat,
            outputImageHunt,
            Imgproc.TM_CCOEFF_NORMED
        )
        return Core.minMaxLoc(outputImageHunt)
    }

}
