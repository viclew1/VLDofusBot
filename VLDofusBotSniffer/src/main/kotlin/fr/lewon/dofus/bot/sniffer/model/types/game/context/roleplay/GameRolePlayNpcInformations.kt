package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.context.EntityDispositionInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayNpcInformations : GameRolePlayActorInformations() {
	var npcId: Int = 0
	var sex: Boolean = false
	var specialArtworkId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		npcId = stream.readVarShort().toInt()
		sex = stream.readBoolean()
		specialArtworkId = stream.readVarShort().toInt()
	}
}
