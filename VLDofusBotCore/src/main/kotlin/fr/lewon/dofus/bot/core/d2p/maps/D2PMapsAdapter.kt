package fr.lewon.dofus.bot.core.d2p.maps

import fr.lewon.dofus.bot.core.d2p.AbstractLinkedD2PUrlLoaderAdapter
import fr.lewon.dofus.bot.core.d2p.D2PIndex
import fr.lewon.dofus.bot.core.d2p.maps.cell.*
import fr.lewon.dofus.bot.core.d2p.maps.element.GraphicalElement
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import java.nio.charset.Charset
import kotlin.experimental.xor

object D2PMapsAdapter : AbstractLinkedD2PUrlLoaderAdapter(true, 77) {

    const val MAP_CELLS_COUNT = 560
    const val CELL_WIDTH = 86.0f
    const val CELL_HEIGHT = 43.0f
    const val CELL_HALF_WIDTH = CELL_WIDTH / 2f
    const val CELL_HALF_HEIGHT = CELL_HEIGHT / 2f

    lateinit var DECRYPTION_KEY: String
    lateinit var DECRYPTION_KEY_CHARSET: String

    override fun getId(filePath: String): Double {
        return Regex(".*?/([0-9]+)\\.dlm").find(filePath)?.destructured?.component1()?.toDouble()
            ?: error("Invalid key")
    }

    @Synchronized
    fun getMapData(mapId: Double): MapData {
        return deserialize(loadFromData(loadStream(mapId)))
    }

    override fun doLoadStream(index: D2PIndex): ByteArray {
        val fileStream = index.stream ?: error("Stream should be cached")
        fileStream.setPosition(index.offset)
        return fileStream.readNBytes(index.length)
    }

    private fun deserialize(bar: ByteArrayReader): MapData {
        var stream = bar
        val header = stream.readByte().toInt()
        if (header != loaderHeader) {
            error("Unknown file format")
        }
        val mapVersion = stream.readByte().toInt()
        val id = stream.readInt()
        if (mapVersion >= 7) {
            val encrypted = stream.readBoolean()
            val encryptionVersion = stream.readByte()
            val dataLen = stream.readInt()
            if (encrypted) {
                val encryptedData = stream.readNBytes(dataLen)
                val decryptionKey = DECRYPTION_KEY.toByteArray(Charset.forName(DECRYPTION_KEY_CHARSET))
                for (i in encryptedData.indices) {
                    encryptedData[i] = encryptedData[i] xor decryptionKey[i % decryptionKey.size]
                }
                stream = ByteArrayReader(encryptedData)
            }
        }
        val relativeId = stream.readInt()
        val mapType = stream.readByte()
        val subareaId = stream.readInt()
        val topNeighbourId = stream.readInt()
        val bottomNeighbourId = stream.readInt()
        val leftNeighbourId = stream.readInt()
        val rightNeighbourId = stream.readInt()
        val shadowBonusOnEntities = stream.readInt()
        if (mapVersion >= 9) {
            val readColor = stream.readInt()
            stream.readInt()
        } else if (mapVersion >= 3) {
            val backgroundRed = stream.readByte()
            val backgroundGreen = stream.readByte()
            val backgroundBlue = stream.readByte()
        }
        if (mapVersion >= 4) {
            var zoomScale = stream.readUnsignedShort() / 100
            var zoomOffsetX = stream.readUnsignedShort()
            var zoomOffsetY = stream.readUnsignedShort()
            if (zoomScale < 1) {
                zoomScale = 1
                zoomOffsetX = 0
                zoomOffsetY = 0
            }
        }
        if (mapVersion > 10) {
            val tacticalModeTemplateId = stream.readInt()
        }
        val backgroundFixtures = ArrayList<Fixture>()
        val backgroundsCount = stream.readUnsignedByte()
        for (i in 0 until backgroundsCount) {
            val fixture = Fixture()
            fixture.deserialize(stream)
            backgroundFixtures.add(fixture)
        }
        val foregroundFixtures = ArrayList<Fixture>()
        val foregroundsCount = stream.readUnsignedByte()
        for (i in 0 until foregroundsCount) {
            val fixture = Fixture()
            fixture.deserialize(stream)
            foregroundFixtures.add(fixture)
        }
        stream.readInt()
        val groundCRC = stream.readInt()
        val layersCount = stream.readUnsignedByte()
        val completeCellDataById = HashMap<Int, CompleteCellData>()
        val graphicalElementsByCellId = HashMap<Int, ArrayList<GraphicalElement>>()
        for (i in 0 until layersCount) {
            val layer = Layer()
            layer.deserialize(stream, mapVersion)
            if (layer.layerType == Layer.LayerType.LAYER_DECOR || layer.layerType == Layer.LayerType.LAYER_ADDITIONAL_DECOR) {
                layer.cells.forEach {
                    val graphicalElements = graphicalElementsByCellId.computeIfAbsent(it.cellId) { ArrayList() }
                    graphicalElements.addAll(it.graphicalElements)
                }
            }
        }
        for (cellId in 0 until MAP_CELLS_COUNT) {
            val cellData = CellData(cellId)
            cellData.deserialize(stream, mapVersion)
            val graphicalElements = graphicalElementsByCellId[cellId] ?: emptyList()
            completeCellDataById[cellId] = CompleteCellData(cellId, cellData, graphicalElements)
        }
        return MapData(completeCellDataById, backgroundFixtures, foregroundFixtures)
    }

}