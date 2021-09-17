package fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt

import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntFlag
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStep
import fr.lewon.dofus.bot.util.filemanagers.DTBDofusMapManager
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class TreasureHuntMessage : INetworkMessage {

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
        for (i in 0 until stream.readUnsignedShort()) {
            val huntStep = TypeManager.getInstance<TreasureHuntStep>(stream.readUnsignedShort())
            huntStep.deserialize(stream)
            huntSteps.add(huntStep)
        }
        totalStepCount = stream.readByte().toInt()
        checkPointCurrent = stream.readVarInt()
        checkPointTotal = stream.readVarInt()
        availableRetryCount = stream.readInt()
        for (i in 0 until stream.readUnsignedShort()) {
            val huntFlag = TreasureHuntFlag()
            huntFlag.deserialize(stream)
            huntFlags.add(huntFlag)
        }
    }

}