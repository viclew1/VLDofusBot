package fr.lewon.dofus.bot.util.geometry

interface IRectangle<R : IRectangle<R, T>, T : IPoint<T, out Number>> {

    fun getCenter(): T
    fun getTopLeft(): T
    fun getTopRight(): T
    fun getBottomLeft(): T
    fun getBottomRight(): T
    fun getTranslation(delta: T): R

}