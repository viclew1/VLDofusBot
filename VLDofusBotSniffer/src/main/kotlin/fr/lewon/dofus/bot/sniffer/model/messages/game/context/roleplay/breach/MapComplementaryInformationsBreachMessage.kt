package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.breach

import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightCommonInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightStartingPositions
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GameRolePlayActorInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.breach.BreachBranch
import fr.lewon.dofus.bot.sniffer.model.types.game.house.HouseInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.InteractiveElement
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.MapObstacle
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.StatedElement
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MapComplementaryInformationsBreachMessage : MapComplementaryInformationsDataMessage() {
	var floor: Int = 0
	var room: Int = 0
	var infinityMode: Int = 0
	var branches: ArrayList<BreachBranch> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		floor = stream.readVarInt().toInt()
		room = stream.readUnsignedByte().toInt()
		infinityMode = stream.readUnsignedShort().toInt()
		branches = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<BreachBranch>(stream.readUnsignedShort())
			item.deserialize(stream)
			branches.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9740
}
