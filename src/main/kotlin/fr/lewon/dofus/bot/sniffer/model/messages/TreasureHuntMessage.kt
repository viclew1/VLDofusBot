package fr.lewon.dofus.bot.sniffer.model.messages

import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntFlag
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStep
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader
import fr.lewon.dofus.bot.util.filemanagers.DTBDofusMapManager

class TreasureHuntMessage : INetworkType {

    var questType: Int = -1
    lateinit var startMap: DofusMap
    var huntSteps = ArrayList<TreasureHuntStep>()
    var huntFlags = ArrayList<TreasureHuntFlag>()
    var totalStepCount: Int = -1
    var checkPointCurrent: Int = -1
    var checkPointTotal: Int = -1
    var availableRetryCount: Int = -1

    override fun deserialize(stream: ByteArrayReader) {
        questType = stream.readByte().toInt()
        val startMapId = stream.readDouble()
        startMap = DTBDofusMapManager.getDofusMap(startMapId)
        for (i in 0 until stream.readShort()) {
            val huntStep = TypeManager.getInstance<TreasureHuntStep>(stream.readShort())
            huntStep.deserialize(stream)
            huntSteps.add(huntStep)
        }
        totalStepCount = stream.readByte().toInt()
        checkPointCurrent = stream.readVarInt()
        checkPointTotal = stream.readVarInt()
        availableRetryCount = stream.readInt()
        for (i in 0 until stream.readShort()) {
            val huntFlag = TreasureHuntFlag()
            huntFlag.deserialize(stream)
            huntFlags.add(huntFlag)
        }
    }

}