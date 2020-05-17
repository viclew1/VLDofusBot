package fr.lewon.dofus.bot.util

import fr.lewon.dofus.bot.util.ImageUtil.bufferedImageToMat
import fr.lewon.dofus.bot.util.ImageUtil.resizeImage
import fr.lewon.dofus.bot.util.fight.FightBoard
import fr.lewon.dofus.bot.util.fight.FightCell
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
        val enemyPlayerColor1 = Color(43, 40, 206).rgb
        val enemyPlayerColor2 = Color(45, 43, 209).rgb
        val enemyPlayerColor3 = Color(83, 79, 71).rgb
        val enemyPlayerColor4 = Color(74, 70, 102).rgb
        val enemyPlayerColor5 = Color(74, 71, 103).rgb
        val enemyPlayerColor6 = Color(86, 82, 61).rgb
        val moveColor1 = Color(90, 125, 62).rgb
        val moveColor2 = Color(85, 121, 56).rgb

        fightBoard.accessibleCells.clear()

        val exploredByRow = mutableMapOf<Int, MutableList<Int>>()
        for (row in 0 until gameImage.height) {
            exploredByRow[row] = ArrayList()
        }

        val enemyPosPoints = ArrayList<Pair<Int, Int>>()

        val accessibleCells = ArrayList<FightCell>()
        for (row in 0 until gameImage.height) {
            for (col in 0 until gameImage.width) {
                if (exploredByRow[row]?.contains(col) == false) {
                    val color = gameImage.getRGB(col, row)
                    if (color == enemyPlayerColor1 || color == enemyPlayerColor2 || color == enemyPlayerColor3 || color == enemyPlayerColor4 || color == enemyPlayerColor5 || color == enemyPlayerColor6) {
                        getCluster(gameImage, row, col, color, exploredByRow)
                            .takeIf { it.size > 10 }
                            ?.let { enemyPosPoints.addAll(it) }
                    } else if (color == moveColor1 || color == moveColor2) {
                        treatPixel(gameImage, row, col, 200, color, exploredByRow)
                            ?.let { accessibleCells.add(it) }
                    }
                }
            }
        }

        val enemyTop = enemyPosPoints.minBy { it.second } ?: Pair(0, 0)
        val enemyBot = enemyPosPoints.maxBy { it.second } ?: Pair(0, 0)
        val enemyLeft = enemyPosPoints.minBy { it.first } ?: Pair(0, 0)
        val enemyRight = enemyPosPoints.maxBy { it.first } ?: Pair(0, 0)

        val newEnemyFightCell = FightCell(
            Rectangle(
                enemyLeft.first,
                enemyTop.second,
                enemyRight.first - enemyLeft.first,
                enemyBot.second - enemyTop.second
            )
        )

        val newEnemyFightCellCenter = newEnemyFightCell.getCenter()
        fightBoard.enemyPos = fightBoard.cells
            .minBy { abs(it.bounds.centerX.toInt() - newEnemyFightCellCenter.first) + abs(it.bounds.centerY.toInt() - newEnemyFightCellCenter.second) }
            ?: error("Couldn't find new enemy position")
        for (cell in fightBoard.cells) {
            val center = cell.getCenter()
            for (access in accessibleCells) {
                if (access.bounds.contains(center.first, center.second)) {
                    fightBoard.accessibleCells.add(cell)
                    break
                }
            }
        }
    }

    fun getFightBoard(gameImage: BufferedImage): FightBoard {
        val yourStart = Color(221, 34, 0).rgb
        val enemyStart1 = Color(100, 104, 131).rgb
        val enemyStart2 = Color(106, 110, 137).rgb
        val stdColor1 = Color(150, 142, 103).rgb
        val stdColor2 = Color(142, 134, 94).rgb

        val yourPlayerColor1 = Color(245, 10, 0).rgb
        val yourPlayerColor2 = Color(100, 79, 54).rgb
        val enemyPlayerColor1 = Color(30, 31, 217).rgb
        val enemyPlayerColor2 = Color(32, 33, 219).rgb

        val startTiles = ArrayList<FightCell>()
        val tiles = ArrayList<FightCell>()
        val yourPosPoints = ArrayList<Pair<Int, Int>>()
        val enemyPosPoints = ArrayList<Pair<Int, Int>>()

        val exploredByRow = mutableMapOf<Int, MutableList<Int>>()
        for (row in 0 until gameImage.height) {
            exploredByRow[row] = ArrayList()
        }
        for (row in 0 until gameImage.height) {
            for (col in 0 until gameImage.width) {
                if (exploredByRow[row]?.contains(col) == false) {
                    val color = gameImage.getRGB(col, row)
                    if (color == enemyStart1 || color == enemyStart2 || color == stdColor1 || color == stdColor2) {
                        treatPixel(gameImage, row, col, 200, color, exploredByRow)
                            ?.let { tiles.add(it) }
                    } else if (color == yourPlayerColor1 || color == yourPlayerColor2) {
                        getCluster(gameImage, row, col, color, exploredByRow)
                            .takeIf { it.size > 10 }
                            ?.let { yourPosPoints.addAll(it) }
                    } else if (color == enemyPlayerColor1 || color == enemyPlayerColor2) {
                        getCluster(gameImage, row, col, color, exploredByRow)
                            .takeIf { it.size > 10 }
                            ?.let { enemyPosPoints.addAll(it) }
                    } else if (color == yourStart) {
                        treatPixel(gameImage, row, col, 200, color, exploredByRow)
                            ?.let { tiles.add(it); startTiles.add(it) }
                    }
                }
            }
        }

        val youTop = yourPosPoints.minBy { it.second } ?: Pair(0, 0)
        val youBot = yourPosPoints.maxBy { it.second } ?: Pair(0, 0)
        val youLeft = yourPosPoints.minBy { it.first } ?: Pair(0, 0)
        val youRight = yourPosPoints.maxBy { it.first } ?: Pair(0, 0)

        val enemyTop = enemyPosPoints.minBy { it.second } ?: Pair(0, 0)
        val enemyBot = enemyPosPoints.maxBy { it.second } ?: Pair(0, 0)
        val enemyLeft = enemyPosPoints.minBy { it.first } ?: Pair(0, 0)
        val enemyRight = enemyPosPoints.maxBy { it.first } ?: Pair(0, 0)

        val yourFightCell = FightCell(
            Rectangle(youLeft.first, youTop.second, youRight.first - youLeft.first, youBot.second - youTop.second)
        )

        val enemyFightCell = FightCell(
            Rectangle(
                enemyLeft.first,
                enemyTop.second,
                enemyRight.first - enemyLeft.first,
                enemyBot.second - enemyTop.second
            )
        )

        tiles.add(yourFightCell)
        tiles.add(enemyFightCell)

        return FightBoard(tiles, startTiles, yourFightCell, enemyFightCell)
    }

    private fun treatPixel(
        img: BufferedImage,
        row: Int,
        col: Int,
        minClusterSize: Int,
        color: Int,
        exploredByRow: Map<Int, MutableList<Int>>
    ): FightCell? {
        val cluster = getCluster(img, row, col, color, exploredByRow)
        if (cluster.size > minClusterSize) {
            val top = cluster.minBy { it.second } ?: Pair(0, 0)
            val bot = cluster.maxBy { it.second } ?: Pair(0, 0)
            val left = cluster.minBy { it.first } ?: Pair(0, 0)
            val right = cluster.maxBy { it.first } ?: Pair(0, 0)
            return FightCell(
                Rectangle(left.first, top.second, right.first - left.first, bot.second - top.second)
            )
        }
        return null
    }

    private fun getCluster(
        img: BufferedImage,
        row: Int,
        col: Int,
        color: Int,
        exploredByRow: Map<Int, MutableList<Int>>
    ): List<Pair<Int, Int>> {
        val colouredTiles = mutableListOf(Pair(col, row))
        var frontier = listOf(Pair(col, row))
        val explored = mutableListOf(Pair(col, row))
        while (frontier.isNotEmpty()) {
            val newFrontier = ArrayList<Pair<Int, Int>>()
            for (n in frontier) {
                colouredTiles.add(n)
                exploredByRow[n.second]?.add(n.first)
                val neighbors = listOf(
                    Pair(n.first + 1, n.second),
                    Pair(n.first - 1, n.second),
                    Pair(n.first, n.second + 1),
                    Pair(n.first, n.second - 1)
                )
                for (neighbor in neighbors) {
                    if (!explored.contains(neighbor)) {
                        explored.add(neighbor)
                        if (img.getRGB(neighbor.first, neighbor.second) == color) {
                            newFrontier.add(neighbor)
                        }
                    }
                }
            }
            frontier = newFrontier
        }
        return colouredTiles
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
        var locationImage = gameImage.getSubimage(
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