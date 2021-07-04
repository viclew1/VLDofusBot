package fr.lewon.dofus.bot.util.filemanagers

import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader
import java.io.File

object DTBDofusMapManager {

    private val mapByKey = HashMap<Double, DofusMap>()

    init {
        val dataDirPath = System.getProperty("user.home") + "/AppData/Local/Ankama/zaap/dofus/data/"
        val d2oMapFile = File("$dataDirPath/common/MapPositions.d2o")
        val stream = ByteArrayReader(d2oMapFile.readBytes())
        require("D2O" == stream.readString(3)) { error("Invalid D2O file") }
        stream.readInt()
        while (stream.readInt() == 1) {
            val id = stream.readDouble()
            val posX = stream.readInt()
            val posY = stream.readInt()
            stream.readBoolean()
            stream.readInt()
            stream.readInt()
            stream.readBoolean()
            for (i in 0 until stream.readInt()) {
                for (j in 0 until stream.readInt()) {
                    stream.readInt()
                }
            }
            val subAreaId = stream.readInt()
            val worldMap = stream.readInt()
            stream.skip(9)
            mapByKey[id] = DofusMap(subAreaId, worldMap, id, posX, posY)
        }
    }

    fun getDofusMap(id: Double): DofusMap {
        return mapByKey[id] ?: error("No map found for id [$id]")
    }

}