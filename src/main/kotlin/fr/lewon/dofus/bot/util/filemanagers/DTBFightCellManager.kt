package fr.lewon.dofus.bot.util.filemanagers

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.util.io.d2p.D2PUtil
import fr.lewon.dofus.bot.util.io.d2p.MapsAdapter
import fr.lewon.dofus.bot.util.io.d2p.cell.CellData
import java.io.File

object DTBFightCellManager {

    const val MAP_CELLS_COUNT = 560

    init {
        val mapsPath = System.getProperty("user.home") + "/AppData/Local/Ankama/zaap/dofus/content/maps"
        File(mapsPath).listFiles()
            ?.filter { it.absolutePath.endsWith(".d2p") }
            ?.forEach { D2PUtil.initStream(it.absolutePath) ?: error("Failed to init stream") }
            ?: error("Maps directory not found")
    }

    fun updateFightBoard(mapId: Double, key: String) {
        val cellDataList = getCellDataList(mapId, key)
        GameInfo.fightBoard.updateCells(cellDataList)
    }

    private fun getCellDataList(mapId: Double, key: String): List<CellData> {
        val index = D2PUtil.indexes[mapId] ?: error("Missing map : $mapId")
        val fileStream = index.stream
        fileStream.setPosition(index.offset)
        val data = fileStream.readNBytes(index.length)
        return MapsAdapter.loadFromData(data, key.toByteArray())
    }

}