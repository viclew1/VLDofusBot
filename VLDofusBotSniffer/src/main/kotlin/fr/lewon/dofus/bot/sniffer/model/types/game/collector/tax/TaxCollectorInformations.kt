package fr.lewon.dofus.bot.sniffer.model.types.game.collector.tax

import fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic.CharacterCharacteristics
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.AllianceInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItem
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TaxCollectorInformations : NetworkType() {
	var uniqueId: Double = 0.0
	var firstNameId: Int = 0
	var lastNameId: Int = 0
	lateinit var allianceIdentity: AllianceInformation
	lateinit var additionalInfos: AdditionalTaxCollectorInformation
	var worldX: Int = 0
	var worldY: Int = 0
	var subAreaId: Int = 0
	var state: Int = 0
	lateinit var look: EntityLook
	var complements: ArrayList<TaxCollectorComplementaryInformations> = ArrayList()
	lateinit var characteristics: CharacterCharacteristics
	var equipments: ArrayList<ObjectItem> = ArrayList()
	var spells: ArrayList<TaxCollectorOrderedSpell> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		uniqueId = stream.readDouble().toDouble()
		firstNameId = stream.readVarShort().toInt()
		lastNameId = stream.readVarShort().toInt()
		allianceIdentity = AllianceInformation()
		allianceIdentity.deserialize(stream)
		additionalInfos = AdditionalTaxCollectorInformation()
		additionalInfos.deserialize(stream)
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
		subAreaId = stream.readVarShort().toInt()
		state = stream.readUnsignedByte().toInt()
		look = EntityLook()
		look.deserialize(stream)
		complements = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<TaxCollectorComplementaryInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			complements.add(item)
		}
		characteristics = CharacterCharacteristics()
		characteristics.deserialize(stream)
		equipments = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ObjectItem()
			item.deserialize(stream)
			equipments.add(item)
		}
		spells = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = TaxCollectorOrderedSpell()
			item.deserialize(stream)
			spells.add(item)
		}
	}
}
