package fr.lewon.dofus.bot.core.ui

data class UIRectangle(var position: UIPoint, var size: UIPoint) {
    fun getCenter(): UIPoint {
        return UIPoint(position.x + size.x / 2, position.y + size.y / 2)
    }
}