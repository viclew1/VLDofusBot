package fr.lewon.dofus.bot.sniffer.model.types.hunt

import fr.lewon.dofus.bot.sniffer.model.INetworkType

abstract class TreasureHuntStep : INetworkType {

    abstract fun getHintLabel(): String

}