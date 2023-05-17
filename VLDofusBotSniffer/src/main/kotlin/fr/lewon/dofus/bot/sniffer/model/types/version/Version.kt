package fr.lewon.dofus.bot.sniffer.model.types.version

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class Version : NetworkType() {
	var major: Int = 0
	var minor: Int = 0
	var code: Int = 0
	var build: Int = 0
	var buildType: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		major = stream.readUnsignedByte().toInt()
		minor = stream.readUnsignedByte().toInt()
		code = stream.readUnsignedByte().toInt()
		build = stream.readInt().toInt()
		buildType = stream.readUnsignedByte().toInt()
	}
}
