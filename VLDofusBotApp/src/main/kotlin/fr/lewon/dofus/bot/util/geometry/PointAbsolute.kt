package fr.lewon.dofus.bot.util.geometry

class PointAbsolute(val x: Int, val y: Int) : IPoint<PointAbsolute, Int> {

    override fun getSum(point: PointAbsolute): PointAbsolute {
        return PointAbsolute(x + point.x, y + point.y)
    }

    override fun getDifference(point: PointAbsolute): PointAbsolute {
        return PointAbsolute(x - point.x, y - point.y)
    }

    override fun getProduct(prod: Int): PointAbsolute {
        return PointAbsolute(x * prod, y * prod)
    }

    override fun opposite(): PointAbsolute {
        return PointAbsolute(-x, -y)
    }

}