package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.context.EntityDispositionInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayHumanoidInformations : GameRolePlayNamedActorInformations() {
	lateinit var humanoidInfo: HumanInformations
	var accountId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		humanoidInfo = ProtocolTypeManager.getInstance<HumanInformations>(stream.readUnsignedShort())
		humanoidInfo.deserialize(stream)
		accountId = stream.readInt().toInt()
	}
}
