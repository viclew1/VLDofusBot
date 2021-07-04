package fr.lewon.dofus.bot.sniffer.model.types.house

import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class HouseOnMapInformations : HouseInformations() {

    var doorsOnMap = ArrayList<Int>()
    var houseInstances = ArrayList<HouseInstanceInformations>()

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        val doorsOnMapCount = stream.readShort()
        for (i in 0 until doorsOnMapCount) {
            doorsOnMap.add(stream.readInt())
        }
        val houseInstancesCount = stream.readShort()
        for (i in 0 until houseInstancesCount) {
            val houseInstance = HouseInstanceInformations()
            houseInstance.deserialize(stream)
            houseInstances.add(houseInstance)
        }
    }
}