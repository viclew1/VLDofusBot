package fr.lewon.dofus.bot.model.maps

import fr.lewon.dofus.bot.util.filemanagers.DTBLabelManager

data class DofusMap(
    var subAreaId: Int = -1,
    var worldMap: Int = -1,
    var id: Double = -1.0,
    var posX: Int = -1,
    var posY: Int = -1
) {
    fun getCoordinates(): DofusCoordinates {
        return DofusCoordinates(posX, posY)
    }

    fun isAltWorld(): Boolean {
        return worldMap != 1
    }

    fun getSubAreaLabel(): String {
        return DTBLabelManager.getSubAreaLabel(subAreaId)
    }

    fun getAreaLabel(): String {
        return DTBLabelManager.getAreaLabel(subAreaId)
    }

}