package fr.lewon.dofus.bot.sniffer.model.messages.game.character.choice

import fr.lewon.dofus.bot.sniffer.model.types.game.character.choice.CharacterBaseInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.character.choice.CharacterToRemodelInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharactersListWithRemodelingMessage : CharactersListMessage() {
	var charactersToRemodel: ArrayList<CharacterToRemodelInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		charactersToRemodel = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = CharacterToRemodelInformations()
			item.deserialize(stream)
			charactersToRemodel.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 2612
}
