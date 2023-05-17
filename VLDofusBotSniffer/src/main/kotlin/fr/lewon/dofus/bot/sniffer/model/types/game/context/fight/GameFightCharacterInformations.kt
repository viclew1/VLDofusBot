package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.character.alignment.ActorAlignmentInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.character.status.PlayerStatus
import fr.lewon.dofus.bot.sniffer.model.types.game.context.EntityDispositionInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightCharacterInformations : GameFightFighterNamedInformations() {
	var level: Int = 0
	lateinit var alignmentInfos: ActorAlignmentInformations
	var breed: Int = 0
	var sex: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		level = stream.readVarShort().toInt()
		alignmentInfos = ActorAlignmentInformations()
		alignmentInfos.deserialize(stream)
		breed = stream.readUnsignedByte().toInt()
		sex = stream.readBoolean()
	}
}
