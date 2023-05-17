package fr.lewon.dofus.bot.core

interface VldbManager {

    fun initManager()

    fun getNeededManagers(): List<VldbManager>

}