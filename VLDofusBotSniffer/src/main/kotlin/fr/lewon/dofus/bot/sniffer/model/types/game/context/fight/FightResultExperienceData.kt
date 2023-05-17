package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FightResultExperienceData : FightResultAdditionalData() {
	var showExperience: Boolean = false
	var showExperienceLevelFloor: Boolean = false
	var showExperienceNextLevelFloor: Boolean = false
	var showExperienceFightDelta: Boolean = false
	var showExperienceForGuild: Boolean = false
	var showExperienceForMount: Boolean = false
	var isIncarnationExperience: Boolean = false
	var experience: Double = 0.0
	var experienceLevelFloor: Double = 0.0
	var experienceNextLevelFloor: Double = 0.0
	var experienceFightDelta: Double = 0.0
	var experienceForGuild: Double = 0.0
	var experienceForMount: Double = 0.0
	var rerollExperienceMul: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		showExperience = BooleanByteWrapper.getFlag(_box0, 0)
		showExperienceLevelFloor = BooleanByteWrapper.getFlag(_box0, 1)
		showExperienceNextLevelFloor = BooleanByteWrapper.getFlag(_box0, 2)
		showExperienceFightDelta = BooleanByteWrapper.getFlag(_box0, 3)
		showExperienceForGuild = BooleanByteWrapper.getFlag(_box0, 4)
		showExperienceForMount = BooleanByteWrapper.getFlag(_box0, 5)
		isIncarnationExperience = BooleanByteWrapper.getFlag(_box0, 6)
		experience = stream.readVarLong().toDouble()
		experienceLevelFloor = stream.readVarLong().toDouble()
		experienceNextLevelFloor = stream.readVarLong().toDouble()
		experienceFightDelta = stream.readVarLong().toDouble()
		experienceForGuild = stream.readVarLong().toDouble()
		experienceForMount = stream.readVarLong().toDouble()
		rerollExperienceMul = stream.readUnsignedByte().toInt()
	}
}
