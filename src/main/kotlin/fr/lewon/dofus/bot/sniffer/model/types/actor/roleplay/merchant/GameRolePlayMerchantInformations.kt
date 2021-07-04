package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.merchant

import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.actor.human.options.HumanOption
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.GameRolePlayNamedActorInformations
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class GameRolePlayMerchantInformations : GameRolePlayNamedActorInformations() {

    var sellType = -1
    var options = ArrayList<HumanOption>()

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        sellType = stream.readByte().toInt()
        for (i in 0 until stream.readShort()) {
            val option = TypeManager.getInstance<HumanOption>(stream.readShort())
            option.deserialize(stream)
            options.add(option)
        }
    }
}