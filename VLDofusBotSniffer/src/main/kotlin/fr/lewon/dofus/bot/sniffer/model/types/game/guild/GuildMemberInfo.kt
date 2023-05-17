package fr.lewon.dofus.bot.sniffer.model.types.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.character.guild.note.PlayerNote
import fr.lewon.dofus.bot.sniffer.model.types.game.character.status.PlayerStatus
import fr.lewon.dofus.bot.sniffer.model.types.game.social.SocialMember
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildMemberInfo : SocialMember() {
	var givenExperience: Double = 0.0
	var experienceGivenPercent: Int = 0
	var alignmentSide: Int = 0
	var moodSmileyId: Int = 0
	var achievementPoints: Int = 0
	var havenBagShared: Boolean = false
	lateinit var note: PlayerNote
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		givenExperience = stream.readVarLong().toDouble()
		experienceGivenPercent = stream.readUnsignedByte().toInt()
		alignmentSide = stream.readUnsignedByte().toInt()
		moodSmileyId = stream.readVarShort().toInt()
		achievementPoints = stream.readInt().toInt()
		havenBagShared = stream.readBoolean()
		note = PlayerNote()
		note.deserialize(stream)
	}
}
