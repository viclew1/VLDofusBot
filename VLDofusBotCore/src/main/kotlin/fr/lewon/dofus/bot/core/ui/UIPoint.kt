package fr.lewon.dofus.bot.core.ui

data class UIPoint(var x: Float = 0f, var y: Float = 0f) {

    fun transpose(x: Float, y: Float): UIPoint {
        return UIPoint(this.x + x, this.y + y)
    }

    fun transpose(point: UIPoint): UIPoint {
        return UIPoint(x + point.x, y + point.y)
    }

    fun invert(): UIPoint {
        return UIPoint(-x, -y)
    }

}