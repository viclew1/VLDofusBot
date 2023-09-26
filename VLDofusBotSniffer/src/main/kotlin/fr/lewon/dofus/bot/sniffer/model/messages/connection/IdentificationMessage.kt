package fr.lewon.dofus.bot.sniffer.model.messages.connection

import fr.lewon.dofus.bot.sniffer.model.types.version.Version
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IdentificationMessage : NetworkMessage() {
	var autoconnect: Boolean = false
	var useCertificate: Boolean = false
	var useLoginToken: Boolean = false
	lateinit var version: Version
	var lang: String = ""
	var credentials: ArrayList<Int> = ArrayList()
	var serverId: Int = 0
	var sessionOptionalSalt: Double = 0.0
	var failedAttempts: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		autoconnect = BooleanByteWrapper.getFlag(_box0, 0)
		useCertificate = BooleanByteWrapper.getFlag(_box0, 1)
		useLoginToken = BooleanByteWrapper.getFlag(_box0, 2)
		version = Version()
		version.deserialize(stream)
		lang = stream.readUTF()
		credentials = ArrayList()
		for (i in 0 until stream.readVarInt().toInt()) {
			val item = stream.readUnsignedByte().toInt()
			credentials.add(item)
		}
		serverId = stream.readUnsignedShort().toInt()
		sessionOptionalSalt = stream.readVarLong().toDouble()
		failedAttempts = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			failedAttempts.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4805
}
