package fr.lewon.dofus.bot.sniffer.model.types.fight.fighter

import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.actor.GameContextActorInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.GameContextBasicSpawnInformation
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.GameFightCharacteristics
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

open class GameFightFighterInformations : GameContextActorInformations() {

    var spawnInfo = GameContextBasicSpawnInformation()
    var wave = 0
    lateinit var stats: GameFightCharacteristics
    var previousPositions = ArrayList<Int>()

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        spawnInfo.deserialize(stream)
        wave = stream.readByte().toInt()
        stats = TypeManager.getInstance(stream.readUnsignedShort())
        stats.deserialize(stream)
        for (i in 0 until stream.readUnsignedShort()) {
            val previousPos = stream.readVarShort()
            previousPositions.add(previousPos)
        }

    }

}