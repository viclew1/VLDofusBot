package fr.lewon.dofus.bot.util

import fr.lewon.dofus.bot.util.ImageUtil.bufferedImageToMat
import fr.lewon.dofus.bot.util.ImageUtil.resizeImage
import fr.lewon.dofus.bot.util.fight.FightBoard
import fr.lewon.dofus.bot.util.fight.FightCell
import fr.lewon.dofus.bot.util.fight.FightCellType
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.Color
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs


object GameInfoUtil {

    @Synchronized
    fun getButtonBounds(gameImage: BufferedImage, imagePath: String, minMatchValue: Double = 0.6): Rectangle? {
        val searchTemplate = Imgcodecs.imread(imagePath)
        val matchResult = this.getMatchResult(gameImage, searchTemplate) ?: return null
        if (matchResult.maxVal < minMatchValue) return null
        ImageIO.write(
            gameImage.getSubimage(
                matchResult.maxLoc.x.toInt(),
                matchResult.maxLoc.y.toInt(),
                searchTemplate.cols(),
                searchTemplate.rows()
            ),
            "png",
            File("debug/button.png")
        )
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
        val tempTopMat = Imgcodecs.imread(DofusImages.TREASURE_HUNT_FRAME_TOP_TEMPLATE.path)
        val tempBotMat = Imgcodecs.imread(DofusImages.TREASURE_HUNT_FRAME_BOT_TEMPLATE.path)
        val tempLeftMat = Imgcodecs.imread(DofusImages.TREASURE_HUNT_FRAME_LEFT_TEMPLATE.path)

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

        ImageIO.write(
            subImage,
            "png",
            File("debug/arrows.png")
        )

        val lastArrow = OCRUtil.splitByLine(subImage, 1).last()

        ImageIO.write(
            lastArrow,
            "png",
            File("debug/arrow.png")
        )

        val dirMatPairs: List<Pair<Directions, Mat>> = listOf(
            Pair(
                Directions.LEFT,
                bufferedImageToMat(OCRUtil.keepDarkOnImage(Imgcodecs.imread(DofusImages.LEFT_ARROW.path), false))
            ),
            Pair(
                Directions.RIGHT,
                bufferedImageToMat(OCRUtil.keepDarkOnImage(Imgcodecs.imread(DofusImages.RIGHT_ARROW.path), false))
            ),
            Pair(
                Directions.BOTTOM,
                bufferedImageToMat(OCRUtil.keepDarkOnImage(Imgcodecs.imread(DofusImages.BOTTOM_ARROW.path), false))
            ),
            Pair(
                Directions.TOP,
                bufferedImageToMat(OCRUtil.keepDarkOnImage(Imgcodecs.imread(DofusImages.TOP_ARROW.path), false))
            )
        )

        var highestMatch = -1.0
        var dir: Directions? = null
        for (dirMatPair in dirMatPairs) {
            if (dirMatPair.second.cols() <= lastArrow.width && dirMatPair.second.rows() <= lastArrow.height) {
                getMatchResult(lastArrow, dirMatPair.second)
                    ?.maxVal
                    ?.let {
                        if (it > highestMatch) {
                            highestMatch = it
                            dir = dirMatPair.first
                        }
                    }
            }
        }
        return dir
    }

    /**
     * Analyzes the image to find a phorror. Returns true if a phorror is found, else false
     * @param gameImage
     */
    @Synchronized
    fun phorrorOnMap(gameImage: BufferedImage, minMatchValue: Double = 0.21): Boolean {
        return patternFound(
            gameImage,
            minMatchValue,
            DofusImages.BL_PHORROR.path,
            DofusImages.BR_PHORROR.path,
            DofusImages.TL_PHORROR.path,
            DofusImages.TR_PHORROR.path
        )
    }

    fun refreshBoard(fightBoard: FightBoard, gameImage: BufferedImage) {
        val enemyColors = listOf(
            Color(43, 40, 206).rgb,
            Color(45, 43, 209).rgb,
            Color(83, 79, 71).rgb,
            Color(74, 70, 102).rgb,
            Color(74, 71, 103).rgb,
            Color(86, 82, 61).rgb,
            Color(30, 31, 217).rgb,
            Color(32, 33, 219).rgb,
            Color(30, 31, 217).rgb,
            Color(32, 33, 219).rgb,
            Color(82, 78, 72).rgb
        )
        val moveColors = listOf(
            Color(90, 125, 62).rgb,
            Color(85, 121, 56).rgb
        )

        fightBoard.accessibleCells.clear()

        val explored = mutableListOf(fightBoard.yourPos)
        var frontier = listOf(fightBoard.yourPos)
        while (frontier.isNotEmpty()) {
            val newFrontier = ArrayList<FightCell>()
            for (cell in frontier) {
                for (n in cell.neighbors) {
                    if (!explored.contains(n) && n.fightCellType == FightCellType.ACCESSIBLE) {
                        explored.add(n)
                        val bounds = n.bounds
                        val color = gameImage.getRGB(bounds.x + bounds.width / 2, bounds.y + bounds.height / 4)
                        if (moveColors.contains(color)) {
                            fightBoard.accessibleCells.add(n)
                            newFrontier.add(n)
                        }
                    }
                }
            }
            frontier = newFrontier
        }

        fightBoard.enemyPos = findCharacterTile(gameImage, enemyColors, fightBoard)
    }

    private fun findCharacterTile(gameImage: BufferedImage, colors: List<Int>, fightBoard: FightBoard): FightCell {
        val explored = mutableListOf(fightBoard.enemyPos)
        var frontier = listOf(fightBoard.enemyPos)
        while (frontier.isNotEmpty()) {
            val newFrontier = ArrayList<FightCell>()
            for (cell in frontier) {
                if (colorCount(gameImage, cell, colors) >= 100) {
                    return cell
                }
                for (n in cell.neighbors) {
                    if (!explored.contains(n) && n.fightCellType == FightCellType.ACCESSIBLE) {
                        explored.add(n)
                        newFrontier.add(n)
                    }
                }
            }
            frontier = newFrontier
        }
        error("Could not find new enemy position")
    }

    fun getFightBoard(gameImage: BufferedImage): FightBoard {
        val playerColors = listOf(
            Color(245, 10, 0).rgb,
            Color(100, 79, 54).rgb
        )
        val enemyColors = listOf(
            Color(43, 40, 206).rgb,
            Color(45, 43, 209).rgb,
            Color(83, 79, 71).rgb,
            Color(74, 70, 102).rgb,
            Color(74, 71, 103).rgb,
            Color(86, 82, 61).rgb,
            Color(30, 31, 217).rgb,
            Color(32, 33, 219).rgb,
            Color(30, 31, 217).rgb,
            Color(32, 33, 219).rgb,
            Color(82, 78, 72).rgb
        )
        val playerStartColors = listOf(
            Color(221, 34, 0).rgb
        )
        val holeColors = listOf(
            Color(0, 0, 0).rgb
        )
        val wallColors = listOf(
            Color(61, 58, 40).rgb,
            Color(79, 75, 52).rgb
        )

        val startTiles = ArrayList<FightCell>()
        val tiles = ArrayList<FightCell>()

        val tileWidth = (gameImage.width - 650).toDouble() / 14.5
        val tileHeight = (gameImage.height - 200).toDouble() / 20.0
        val initialX = 325 + tileWidth / 2.0
        val initialY = 23 + tileHeight / 4.0

        var playerTile: FightCell? = null
        var enemyTile: FightCell? = null

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
                    playerStartColors.contains(color) -> {
                        val tile = FightCell(currentTileRow, currentTileCol, tileBounds, FightCellType.ACCESSIBLE)
                        startTiles.add(tile)
                        tile
                    }
                    holeColors.contains(color) -> {
                        FightCell(currentTileRow, currentTileCol, tileBounds, FightCellType.HOLE)
                    }
                    wallColors.contains(color) -> {
                        FightCell(currentTileRow, currentTileCol, tileBounds, FightCellType.WALL)
                    }
                    else -> {
                        FightCell(currentTileRow, currentTileCol, tileBounds, FightCellType.ACCESSIBLE)
                    }
                }
                tiles.add(tile)
            }
        }

        for (tile in tiles) {
            if (playerTile == null && colorCount(gameImage, tile, playerColors) >= 100) {
                playerTile = tile
            } else if (enemyTile == null && colorCount(gameImage, tile, enemyColors) >= 100) {
                enemyTile = tile
            }
            if (playerTile != null && enemyTile != null) {
                break
            }
        }

        playerTile ?: error("Cannot find player character")
        enemyTile ?: error("Cannot find enemy character")

        return FightBoard(tiles, startTiles, playerTile, enemyTile)
    }

    private fun colorCount(img: BufferedImage, cell: FightCell, colors: List<Int>): Int {
        val bounds = cell.bounds
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
    fun patternFound(gameImage: BufferedImage, minMatchValue: Double = 0.21, vararg templatesPath: String): Boolean {
        val imgMat = OCRUtil.segmentImage(bufferedImageToMat(gameImage))
        val templates = templatesPath.map { OCRUtil.segmentImage(Imgcodecs.imread(it)) }

        //Match all the templates, if any of them has a value of more than 0.14, we consider the phorror found
        for (template in templates) {
            val matchValue = getMatchResult(imgMat, template)?.maxVal ?: -1.0
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

            ImageIO.write(
                treatedImage,
                "png",
                File("debug/loc.png")
            )

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

        ImageIO.write(
            content,
            "png",
            File("debug/frame_content.png")
        )
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
        val tempTopMat = Imgcodecs.imread(topFramePath)
        val tempBotMat = Imgcodecs.imread(botFramePath)
        val tempLeftMat = Imgcodecs.imread(leftFramePath)
        val tempRightMat = Imgcodecs.imread(rightFramePath)

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

fun main() {
    val img = ImageIO.read(File("templates/test_fight.png"))
    val start = System.currentTimeMillis()
    val board = GameInfoUtil.getFightBoard(img)
    println(System.currentTimeMillis() - start)
    val playerPos = board.yourPos
    val enemyPos = board.enemyPos
    println("${playerPos.col} ; ${playerPos.row}")
    println("${enemyPos.col} ; ${enemyPos.row}")
    println(board.getDist(playerPos, enemyPos))

    val pos29_7 = board.cellsByPosition[Pair(29, 7)] ?: error("")
    println(board.lineOfSight(pos29_7, enemyPos))


    val enemyColors = listOf(
        Color(43, 40, 206).rgb,
        Color(45, 43, 209).rgb,
        Color(83, 79, 71).rgb,
        Color(74, 70, 102).rgb,
        Color(74, 71, 103).rgb,
        Color(86, 82, 61).rgb,
        Color(30, 31, 217).rgb,
        Color(32, 33, 219).rgb,
        Color(30, 31, 217).rgb,
        Color(32, 33, 219).rgb,
        Color(82, 78, 72).rgb
    )
    val img2 = ImageIO.read(File("scripts_templates/fight/test_enemy_loc.png"))
    var cpt = 0
    for (x in 0 until img2.width) {
        for (y in 0 until img2.height) {
            val color = img2.getRGB(x, y)
            if (enemyColors.contains(color)) {
                img2.setRGB(x, y, Color.RED.rgb)
                cpt++
            }
        }
    }
    ImageIO.write(img2, "PNG", File("debug/testtest.png"))
    println(cpt)

}