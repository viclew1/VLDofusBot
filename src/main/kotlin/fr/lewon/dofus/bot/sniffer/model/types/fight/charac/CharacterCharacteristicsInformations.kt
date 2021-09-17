package fr.lewon.dofus.bot.sniffer.model.types.fight.charac

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.actor.ActorExtendedAlignmentInformations
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class CharacterCharacteristicsInformations : INetworkType {

    var experience = 0L
    var experienceLevelFloor = 0L
    var experienceNextLevelFloor = 0L
    var experienceBonusLimit = 0L
    var kamas = 0L
    lateinit var alignmentInfos: ActorExtendedAlignmentInformations
    var criticalHitWeapon = 0
    var characteristics = ArrayList<CharacterCharacteristic>()
    var spellModifications = ArrayList<CharacterSpellModification>()
    var probationTime = 0

    override fun deserialize(stream: ByteArrayReader) {
        experience = stream.readVarLong()
        experienceLevelFloor = stream.readVarLong()
        experienceNextLevelFloor = stream.readVarLong()
        experienceBonusLimit = stream.readVarLong()
        kamas = stream.readVarLong()
        alignmentInfos = ActorExtendedAlignmentInformations()
        alignmentInfos.deserialize(stream)
        criticalHitWeapon = stream.readVarShort()
        for (i in 0 until stream.readUnsignedShort()) {
            val id = stream.readUnsignedShort()
            val item = TypeManager.getInstance<CharacterCharacteristic>(id)
            item.deserialize(stream)
            this.characteristics.add(item)
        }
        for (i in 0 until stream.readUnsignedShort()) {
            val item = CharacterSpellModification()
            item.deserialize(stream)
            spellModifications.add(item)
        }
        probationTime = stream.readInt()
    }

}