package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.character.alignment.ActorAlignmentInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.EntityDispositionInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightMonsterWithAlignmentInformations : GameFightMonsterInformations() {
	lateinit var alignmentInfos: ActorAlignmentInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		alignmentInfos = ActorAlignmentInformations()
		alignmentInfos.deserialize(stream)
	}
}
