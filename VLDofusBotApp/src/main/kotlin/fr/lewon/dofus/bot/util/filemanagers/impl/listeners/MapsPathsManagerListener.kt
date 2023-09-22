package fr.lewon.dofus.bot.util.filemanagers.impl.listeners

import fr.lewon.dofus.bot.model.characters.paths.MapsPathByName

interface MapsPathsManagerListener {

    fun onPathsUpdate(mapPathsByName: MapsPathByName)

}