package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameContextSummonsInformation : NetworkType() {
	lateinit var spawnInformation: SpawnInformation
	var wave: Int = 0
	lateinit var look: EntityLook
	lateinit var stats: GameFightCharacteristics
	var summons: ArrayList<GameContextBasicSpawnInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		spawnInformation = ProtocolTypeManager.getInstance<SpawnInformation>(stream.readUnsignedShort())
		spawnInformation.deserialize(stream)
		wave = stream.readUnsignedByte().toInt()
		look = EntityLook()
		look.deserialize(stream)
		stats = ProtocolTypeManager.getInstance<GameFightCharacteristics>(stream.readUnsignedShort())
		stats.deserialize(stream)
		summons = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<GameContextBasicSpawnInformation>(stream.readUnsignedShort())
			item.deserialize(stream)
			summons.add(item)
		}
	}
}
