package fr.lewon.dofus.bot.util.filemanagers

interface ToInitManager {

    fun initManager()

    fun getNeededManagers(): List<ToInitManager>

}