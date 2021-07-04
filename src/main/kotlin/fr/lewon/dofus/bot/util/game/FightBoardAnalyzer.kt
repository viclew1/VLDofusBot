package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.FightCell
import fr.lewon.dofus.bot.game.fight.FightCellType
import fr.lewon.dofus.bot.game.fight.FightColors
import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.util.io.ScreenUtil
import java.awt.Rectangle
import java.awt.image.BufferedImage

object FightBoardAnalyzer {

    fun buildFightBoard(): FightBoard {
        val startTiles = ArrayList<FightCell>()
        val tiles = ArrayList<FightCell>()

        val tileWidth = GameInfo.fightBounds.width.toFloat() / 14.5
        val tileHeight = GameInfo.fightBounds.width.toFloat() / 20.0
        val initialX = tileWidth / 2.0
        val initialY = tileHeight / 4.0

        val gameImage = ScreenUtil.takeScreenshot(GameInfo.fightBounds)

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

        val playerTile = findCharacterTile(gameImage, tiles, FightColors.playerColors)
            ?: error("Cannot find player character")
        val enemyTile = findCharacterTile(gameImage, tiles, FightColors.enemyColors)
            ?: error("Cannot find enemy character")

        return FightBoard(tiles, startTiles, playerTile, enemyTile).also { it.init() }
    }

    fun refreshFightBoard(fightBoard: FightBoard, gameImage: BufferedImage) {
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

        findCharacterTile(gameImage, fightBoard.cells, FightColors.enemyColors)
            ?.let { fightBoard.enemyPos = it }
//        findCharacterTile(gameImage, FightColors.playerColors, fightBoard)
//            ?.let { fightBoard.playerPos = it }
    }

    private fun findCharacterTile(gameImage: BufferedImage, cells: List<FightCell>, colors: List<Int>): FightCell? {
        return cells
            .filter { it.fightCellType == FightCellType.ACCESSIBLE }
            .maxBy { colorCount(gameImage, it.bounds, colors) }
    }

    private fun colorCount(img: BufferedImage, bounds: Rectangle, colors: List<Int>): Int {
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

}