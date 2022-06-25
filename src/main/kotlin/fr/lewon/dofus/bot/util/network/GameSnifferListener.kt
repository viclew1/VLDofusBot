package fr.lewon.dofus.bot.util.network

import fr.lewon.dofus.bot.model.characters.DofusCharacter

interface GameSnifferListener {

    fun onListenStart(character: DofusCharacter)

    fun onListenStop(character: DofusCharacter)

}