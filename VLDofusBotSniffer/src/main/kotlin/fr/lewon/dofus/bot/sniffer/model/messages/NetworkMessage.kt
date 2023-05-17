package fr.lewon.dofus.bot.sniffer.model.messages

import com.fasterxml.jackson.annotation.JsonIgnore
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType

abstract class NetworkMessage : NetworkType() {

    @JsonIgnore
    abstract fun getNetworkMessageId(): Int

}