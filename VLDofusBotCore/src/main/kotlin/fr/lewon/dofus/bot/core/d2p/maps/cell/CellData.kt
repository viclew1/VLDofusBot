package fr.lewon.dofus.bot.core.d2p.maps.cell

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

data class CellData(
    val cellId: Int,
    var id: Int = 0,
    var speed: Int = 0,
    var mapChangeData: Int = 0,
    var moveZone: Int = 0,
    var losmov: Int = 0,
    var floor: Int = 0,
    var arrow: Int = 0,
    var linkedZone: Int = 0,
    var mov: Boolean = false,
    var los: Boolean = false,
    var nonWalkableDuringFight: Boolean = false,
    var red: Boolean = false,
    var blue: Boolean = false,
    var farmCell: Boolean = false,
    var havenbagCell: Boolean = false,
    var visible: Boolean = false,
    var nonWalkableDuringRP: Boolean = false
) {

    fun hasLinkedZoneRP(): Boolean {
        return this.mov && !this.farmCell
    }

    fun getLinkedZoneRP(): Int {
        return this.linkedZone and 240 shr 4
    }

    fun hasLinkedZoneFight(): Boolean {
        return this.mov && !this.nonWalkableDuringFight && !this.farmCell && !this.havenbagCell
    }

    fun getLinkedZoneFight(): Int {
        return this.linkedZone and 15
    }

    fun deserialize(stream: ByteArrayReader, mapVersion: Int) {
        floor = stream.readByte().toInt() * 10
        if (floor == -1280) {
            return
        }
        if (mapVersion >= 9) {
            val tmpbytesv9 = stream.readUnsignedShort()
            mov = tmpbytesv9 and 1 == 0
            nonWalkableDuringFight = tmpbytesv9 and 2 != 0
            nonWalkableDuringRP = tmpbytesv9 and 4 != 0
            los = tmpbytesv9 and 8 == 0
            blue = tmpbytesv9 and 16 != 0
            red = tmpbytesv9 and 32 != 0
            visible = tmpbytesv9 and 64 != 0
            farmCell = tmpbytesv9 and 128 != 0
            if (mapVersion >= 10) {
                havenbagCell = tmpbytesv9 and 256 != 0
            }
        } else {
            losmov = stream.readByte().toInt()
            los = losmov and 2 shr 1 == 1
            mov = losmov and 1 == 1
            visible = losmov and 64 shr 6 == 1
            farmCell = losmov and 32 shr 5 == 1
            blue = losmov and 16 shr 4 == 1
            red = losmov and 8 shr 3 == 1
            nonWalkableDuringRP = losmov and 128 shr 7 == 1
            nonWalkableDuringFight = losmov and 4 shr 2 == 1
        }
        speed = stream.readByte().toInt()
        mapChangeData = stream.readByte().toInt()
        if (mapVersion > 5) {
            moveZone = stream.readByte().toInt()
        }
        if (mapVersion > 10 && (hasLinkedZoneRP() || hasLinkedZoneFight())) {
            linkedZone = stream.readByte().toInt()
        }
        if (mapVersion == 8) {
            val tmpBits = stream.readByte().toInt()
            arrow = 15 and tmpBits
        }
    }

}