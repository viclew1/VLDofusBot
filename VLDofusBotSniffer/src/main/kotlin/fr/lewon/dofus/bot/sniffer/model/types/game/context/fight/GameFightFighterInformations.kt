package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.context.EntityDispositionInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.GameContextActorInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightFighterInformations : GameContextActorInformations() {
	lateinit var spawnInfo: GameContextBasicSpawnInformation
	var wave: Int = 0
	lateinit var stats: GameFightCharacteristics
	var previousPositions: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		spawnInfo = GameContextBasicSpawnInformation()
		spawnInfo.deserialize(stream)
		wave = stream.readUnsignedByte().toInt()
		stats = ProtocolTypeManager.getInstance<GameFightCharacteristics>(stream.readUnsignedShort())
		stats.deserialize(stream)
		previousPositions = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			previousPositions.add(item)
		}
	}
}
