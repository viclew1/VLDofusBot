package fr.lewon.dofus.bot.sniffer.model.types.hunt

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class TreasureHuntStepFight : TreasureHuntStep() {
    override fun getHintLabel(): String {
        return "FIGHT"
    }

    override fun deserialize(stream: ByteArrayReader) {}
}