package fr.lewon.dofus.bot.util.geometry

class RectangleRelative(val x: Float, val y: Float, val width: Float, val height: Float) : IRectangle<PointRelative> {

    companion object {

        fun build(topLeft: PointRelative, bottomRight: PointRelative): RectangleRelative {
            return RectangleRelative(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y)
        }

    }

    override fun getCenter(): PointRelative {
        return PointRelative(x + width / 2f, y + height / 2f)
    }

    override fun getTopLeft(): PointRelative {
        return PointRelative(x, y)
    }

    override fun getTopRight(): PointRelative {
        return PointRelative(x + width, y)
    }

    override fun getBottomLeft(): PointRelative {
        return PointRelative(x, y + height)
    }

    override fun getBottomRight(): PointRelative {
        return PointRelative(x + width, y + height)
    }

}