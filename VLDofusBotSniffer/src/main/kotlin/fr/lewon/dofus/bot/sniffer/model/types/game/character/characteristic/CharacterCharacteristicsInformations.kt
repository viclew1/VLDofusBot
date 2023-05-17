package fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic

import fr.lewon.dofus.bot.sniffer.model.types.game.character.alignment.ActorExtendedAlignmentInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterCharacteristicsInformations : NetworkType() {
	var experience: Double = 0.0
	var experienceLevelFloor: Double = 0.0
	var experienceNextLevelFloor: Double = 0.0
	var experienceBonusLimit: Double = 0.0
	var kamas: Double = 0.0
	lateinit var alignmentInfos: ActorExtendedAlignmentInformations
	var criticalHitWeapon: Int = 0
	var characteristics: ArrayList<CharacterCharacteristic> = ArrayList()
	var spellModifications: ArrayList<CharacterSpellModification> = ArrayList()
	var probationTime: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		experience = stream.readVarLong().toDouble()
		experienceLevelFloor = stream.readVarLong().toDouble()
		experienceNextLevelFloor = stream.readVarLong().toDouble()
		experienceBonusLimit = stream.readVarLong().toDouble()
		kamas = stream.readVarLong().toDouble()
		alignmentInfos = ActorExtendedAlignmentInformations()
		alignmentInfos.deserialize(stream)
		criticalHitWeapon = stream.readVarShort().toInt()
		characteristics = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<CharacterCharacteristic>(stream.readUnsignedShort())
			item.deserialize(stream)
			characteristics.add(item)
		}
		spellModifications = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = CharacterSpellModification()
			item.deserialize(stream)
			spellModifications.add(item)
		}
		probationTime = stream.readDouble().toDouble()
	}
}
