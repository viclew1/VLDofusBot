package fr.lewon.dofus.bot.util.filemanagers.impl.listeners

import fr.lewon.dofus.bot.model.characters.paths.MapsPath

interface MapsPathsManagerListener {

    fun onPathsUpdate(mapPathsByName: Map<String, MapsPath>)

}