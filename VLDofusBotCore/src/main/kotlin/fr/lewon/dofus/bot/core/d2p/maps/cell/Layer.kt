package fr.lewon.dofus.bot.core.d2p.maps.cell

import fr.lewon.dofus.bot.core.d2p.maps.D2PMapsAdapter
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

class Layer {

    var layerType: LayerType? = null
    val cells = ArrayList<Cell>()

    fun deserialize(stream: ByteArrayReader, mapVersion: Int) {
        val layerId = if (mapVersion >= 9) {
            stream.readUnsignedByte()
        } else {
            stream.readInt()
        }
        layerType = LayerType.fromId(layerId)
        val cellsCount = stream.readUnsignedShort()
        if (cellsCount > 0) {
            var cell: Cell? = null
            for (i in 0 until cellsCount) {
                cell = Cell()
                cell.deserialize(stream, mapVersion)
                cells.add(cell)
            }
            val maxMapCellId = D2PMapsAdapter.MAP_CELLS_COUNT - 1
            if (cell != null && cell.cellId < maxMapCellId) {
                val endCell = Cell().also { it.cellId = maxMapCellId }
                cells.add(endCell)
            }
        }
    }

    enum class LayerType(private val id: Int) {
        LAYER_GROUND(0),
        LAYER_ADDITIONAL_GROUND(1),
        LAYER_DECOR(2),
        LAYER_ADDITIONAL_DECOR(3);

        companion object {
            fun fromId(id: Int): LayerType? {
                return values().firstOrNull { it.id == id }
            }
        }
    }

}