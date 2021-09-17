package fr.lewon.dofus.bot.util.io.d2p

import fr.lewon.dofus.bot.util.filemanagers.DTBFightCellManager
import fr.lewon.dofus.bot.util.io.d2p.cell.Cell
import fr.lewon.dofus.bot.util.io.d2p.cell.CellData
import fr.lewon.dofus.bot.util.io.d2p.element.BasicElement
import fr.lewon.dofus.bot.util.io.d2p.element.ElementType
import fr.lewon.dofus.bot.util.io.d2p.element.GraphicalElement
import fr.lewon.dofus.bot.util.io.d2p.element.SoundElement
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader
import kotlin.experimental.xor

object MapsAdapter {

    fun loadFromData(data: ByteArray, decryptionKey: ByteArray? = null): List<CellData> {
        var stream = ByteArrayReader(data)
        var header = stream.readByte().toInt()
        if (header != 77) {
            stream.setPosition(0)
            stream = ByteArrayReader(stream.uncompress())

            header = stream.readByte().toInt()
            if (header != 77) {
                error("Invalid maps stream")
            }
        }
        stream.setPosition(0)
        return deserialize(stream, decryptionKey)
    }

    fun deserialize(bar: ByteArrayReader, decryptionKey: ByteArray? = null): List<CellData> {
        var stream = bar
        val header = stream.readByte().toInt()
        if (header != 77) {
            error("Unknown file format")
        }
        val mapVersion = stream.readByte().toInt()
        val id = stream.readInt()
        if (mapVersion >= 7) {
            val encrypted = stream.readBoolean()
            val encryptionVersion = stream.readByte()
            val dataLen = stream.readInt()
            if (encrypted) {
                decryptionKey ?: error("Map decryption key is empty")
                val encryptedData = stream.readNBytes(dataLen)
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
        val backgroundsCount = stream.readByte()
        for (i in 0 until backgroundsCount) {
            readFixture(stream)
        }
        val foregroundsCount = stream.readByte()
        for (i in 0 until foregroundsCount) {
            readFixture(stream)
        }
        val cellsCount = DTBFightCellManager.MAP_CELLS_COUNT
        stream.readInt()
        val groundCRC = stream.readInt()
        val layersCount = stream.readByte()
        val layers = ArrayList<List<Cell>>()
        for (i in 0 until layersCount) {
            layers.add(readLayer(mapVersion, stream))
        }
        val cells = ArrayList<CellData>()
        for (i in 0 until cellsCount) {
            val cd = readCellData(mapVersion, i, stream)
            cells.add(cd)
        }
        return cells
    }

    private fun readFixture(stream: ByteArrayReader) {
        val fixtureId = stream.readInt()
        stream.readUnsignedShort()
        stream.readUnsignedShort()
        val rotation = stream.readUnsignedShort()
        val xScale = stream.readUnsignedShort()
        val yScale = stream.readUnsignedShort()
        val redMultiplier = stream.readByte()
        val greenMultiplier = stream.readByte()
        val blueMultiplier = stream.readByte()
        val alpha = stream.readByte()
    }

    private fun readLayer(mapVersion: Int, stream: ByteArrayReader): List<Cell> {
        val layerId = if (mapVersion >= 9) {
            stream.readByte()
        } else {
            stream.readInt()
        }
        val cellsCount = stream.readUnsignedShort()
        val cells = ArrayList<Cell>()
        if (cellsCount > 0) {
            var cell: Cell? = null
            for (i in 0 until cellsCount) {
                cell = readCell(mapVersion, stream)
                cells.add(cell)
            }
            val maxMapCellId = DTBFightCellManager.MAP_CELLS_COUNT - 1
            if (cell != null && cell.cellId < maxMapCellId) {
                val endCell = Cell(maxMapCellId, 0)
                cells.add(endCell)
            }
        }
        return cells
    }

    private fun readCell(mapVersion: Int, stream: ByteArrayReader): Cell {
        val cellId = stream.readUnsignedShort()
        val elementsCount = stream.readUnsignedShort()
        val cell = Cell(cellId, elementsCount)
        for (i in 0 until elementsCount) {
            val be = getBasicElement(stream.readByte().toInt(), cell)
            be.read(mapVersion, stream)
        }
        return cell
    }

    private fun getBasicElement(typeValue: Int, cell: Cell): BasicElement {
        return when (typeValue) {
            ElementType.GRAPHICAL.typeValue -> GraphicalElement(cell)
            ElementType.SOUND.typeValue -> SoundElement(cell)
            else -> error("Invalid element type : $typeValue")
        }
    }

    private fun readCellData(mapVersion: Int, cellId: Int, stream: ByteArrayReader): CellData {
        val cd = CellData(cellId)
        cd.floor = stream.readByte().toInt() * 10
        if (cd.floor == -1280) {
            return cd
        }
        if (mapVersion >= 9) {
            val tmpbytesv9 = stream.readUnsignedShort()
            cd.mov = tmpbytesv9 and 1 == 0
            cd.nonWalkableDuringFight = tmpbytesv9 and 2 != 0
            cd.nonWalkableDuringRP = tmpbytesv9 and 4 != 0
            cd.los = tmpbytesv9 and 8 == 0
            cd.blue = tmpbytesv9 and 16 != 0
            cd.red = tmpbytesv9 and 32 != 0
            cd.visible = tmpbytesv9 and 64 != 0
            cd.farmCell = tmpbytesv9 and 128 != 0
            if (mapVersion >= 10) {
                cd.havenbagCell = tmpbytesv9 and 256 != 0
            }
        } else {
            cd.losmov = stream.readByte().toInt()
            cd.los = cd.losmov and 2 shr 1 == 1
            cd.mov = cd.losmov and 1 == 1
            cd.visible = cd.losmov and 64 shr 6 == 1
            cd.farmCell = cd.losmov and 32 shr 5 == 1
            cd.blue = cd.losmov and 16 shr 4 == 1
            cd.red = cd.losmov and 8 shr 3 == 1
            cd.nonWalkableDuringRP = cd.losmov and 128 shr 7 == 1
            cd.nonWalkableDuringFight = cd.losmov and 4 shr 2 == 1
        }
        cd.speed = stream.readByte().toInt()
        cd.mapChangeData = stream.readByte().toInt()
        if (mapVersion > 5) {
            cd.moveZone = stream.readByte().toInt()
        }
        if (mapVersion > 10 && (cd.hasLinkedZoneRP() || cd.hasLinkedZoneFight())) {
            cd.linkedZone = stream.readByte().toInt()
        }
        if (mapVersion == 0) {
            val tmpBits = stream.readByte().toInt()
            cd.arrow = 15 and tmpBits
        }
        return cd
    }

}