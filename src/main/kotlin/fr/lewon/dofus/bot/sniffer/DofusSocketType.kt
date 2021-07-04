package fr.lewon.dofus.bot.sniffer

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.messages.*
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader
import fr.lewon.dofus.bot.util.filemanagers.DTBSwfScriptManager
import fr.lewon.dofus.bot.util.ui.Debugger
import java.text.SimpleDateFormat
import java.util.*

enum class DofusSocketType(private val messageClass: Class<out INetworkType>) {

    TREASURE_HUNT_EVENT(TreasureHuntMessage::class.java),
    TREASURE_HUNT_END_EVENT(TreasureHuntFinishedMessage::class.java),
    CHANGE_MAP_EVENT(MapComplementaryInformationsDataMessage::class.java),
    HAVEN_BAG_ACCESS_EVENT(MapComplementaryInformationsDataInHavenBagMessage::class.java),
    MESSAGE_RECEIVED_EVENT(ChatServerMessage::class.java),
    FIGHT_ENTERED_EVENT(GameRolePlayShowActorMessage::class.java),
    MOVE_START_EVENT(GameMapMovementMessage::class.java),
    MOVE_END_EVENT(GameMapMovementConfirmMessage::class.java),
    TURN_READY_EVENT(GameFightTurnReadyMessage::class.java),
    ZAAP_DESTINATIONS_EVENT(ZaapDestinationsMessage::class.java),
    ;

    val id = DTBSwfScriptManager.getUuid(messageClass.simpleName)
    val eventBuilder = { messageClass.getConstructor().newInstance() }

    companion object {
        fun fromSocket(socket: ByteArray): DofusSocketType? {
            val socketId = ByteArrayReader(socket).readShort().toUShort().toInt()
            val swfId = (socketId shr 2)
            val socketType = values().firstOrNull { it.id == swfId }
            socketType ?: printUntreated(swfId)
            return socketType
        }

        private fun printUntreated(swfId: Int) {
            val scriptName = try {
                DTBSwfScriptManager.getScriptName(swfId)
            } catch (e: Exception) {
                "SCRIPT_NOT_FOUND"
            }
            val ts = SimpleDateFormat("HH:mm:ss.SSSXXX").format(Date())
            Debugger.debug("Untreated message received : $ts - [$scriptName:$swfId]")
        }
    }
}