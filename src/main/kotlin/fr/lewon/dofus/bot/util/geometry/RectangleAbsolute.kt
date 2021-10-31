package fr.lewon.dofus.bot.util.geometry

class RectangleAbsolute(val x: Int, val y: Int, val width: Int, val height: Int) :
    IRectangle<RectangleAbsolute, PointAbsolute> {

    companion object {

        fun build(topLeft: PointAbsolute, topRight: PointAbsolute): RectangleAbsolute {
            return RectangleAbsolute(topLeft.x, topLeft.y, topRight.x - topLeft.x, topRight.y - topLeft.y)
        }

    }

    override fun getCenter(): PointAbsolute {
        return PointAbsolute(x + width / 2, y + height / 2)
    }

    override fun getTopLeft(): PointAbsolute {
        return PointAbsolute(x, y)
    }

    override fun getTopRight(): PointAbsolute {
        return PointAbsolute(x + width, y)
    }

    override fun getBottomLeft(): PointAbsolute {
        return PointAbsolute(x, y + height)
    }

    override fun getBottomRight(): PointAbsolute {
        return PointAbsolute(x + width, y + height)
    }

    override fun getTranslation(delta: PointAbsolute): RectangleAbsolute {
        return RectangleAbsolute(x + delta.x, y + delta.y, width, height)
    }
}