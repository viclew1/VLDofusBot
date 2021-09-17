package fr.lewon.dofus.bot.sniffer.model.types.hunt

import fr.lewon.dofus.bot.game.move.Direction
import fr.lewon.dofus.bot.util.filemanagers.DTBLabelManager
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class TreasureHuntStepFollowDirectionToPOI : TreasureHuntStep() {

    lateinit var direction: Direction
    lateinit var label: String

    override fun deserialize(stream: ByteArrayReader) {
        val directionInt = stream.readByte().toInt()
        direction = Direction.fromInt(directionInt) ?: error("Unknown direction : $directionInt")
        val labelId = stream.readVarShort()
        label = DTBLabelManager.getHintLabel(labelId)
    }

    override fun getHintLabel(): String {
        return label
    }
}