package fr.lewon.dofus.bot.sniffer.model.updater

import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType

const val BASE_MESSAGES_PATH = "com.ankamagames.dofus.network.messages"
val DEST_MESSAGES_PATH: String = NetworkMessage::class.java.packageName
const val BASE_TYPES_PATH = "com.ankamagames.dofus.network.types"
val DEST_TYPES_PATH: String = NetworkType::class.java.packageName
const val STREAM_NAME = "stream"
const val DESERIALIZE_FUNC_NAME = "deserialize"
const val EXPORT_DIR = "C:/Dev/bots_workspace/VLDofusBotModulesBuilder/VLDofusBot/VLDofusBotSniffer/src/main/kotlin/"
