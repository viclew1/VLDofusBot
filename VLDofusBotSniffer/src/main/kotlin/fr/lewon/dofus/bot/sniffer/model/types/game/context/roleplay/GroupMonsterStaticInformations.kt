package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GroupMonsterStaticInformations : NetworkType() {
	lateinit var mainCreatureLightInfos: MonsterInGroupLightInformations
	var underlings: ArrayList<MonsterInGroupInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		mainCreatureLightInfos = MonsterInGroupLightInformations()
		mainCreatureLightInfos.deserialize(stream)
		underlings = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = MonsterInGroupInformations()
			item.deserialize(stream)
			underlings.add(item)
		}
	}
}
