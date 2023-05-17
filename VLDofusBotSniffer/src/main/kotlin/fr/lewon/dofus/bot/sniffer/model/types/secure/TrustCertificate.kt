package fr.lewon.dofus.bot.sniffer.model.types.secure

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TrustCertificate : NetworkType() {
	var id: Int = 0
	var hash: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		id = stream.readInt().toInt()
		hash = stream.readUTF()
	}
}
