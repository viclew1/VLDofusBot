package fr.lewon.dofus.bot.util.geometry

interface IPoint<T : IPoint<T, P>, P : Number> {

    fun getSum(point: T): T
    fun getDifference(point: T): T
    fun getProduct(prod: P): T

}