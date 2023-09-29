package fr.lewon.dofus.bot.sniffer

import fr.lewon.dofus.VldbProtocolUpdater
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.exceptions.MessageIdNotFoundException
import fr.lewon.dofus.bot.sniffer.managers.MessageIdByName
import fr.lewon.dofus.bot.sniffer.managers.TypeIdByName
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.export.builder.VldbAbstractExportPackTaskBuilder
import fr.lewon.dofus.export.builder.VldbIdByNameExportPackTaskBuilder
import org.reflections.Reflections
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface

object DofusMessageReceiverUtil {

    private lateinit var messagesById: Map<Int, Class<out NetworkMessage>>

    fun parseMessagePremise(stream: ByteArrayReader, messageId: Int): DofusMessagePremise {
        val messageType = messagesById[messageId] ?: throw MessageIdNotFoundException(messageId)
        return DofusMessagePremise(messageId, messageType, stream)
    }

    fun prepareNetworkManagers(additionalBuilders: List<VldbAbstractExportPackTaskBuilder> = emptyList()) {
        processExport(getExportPackBuilders().plus(additionalBuilders))
        messagesById = Reflections(NetworkMessage::class.java.packageName)
            .getSubTypesOf(NetworkMessage::class.java)
            .groupBy { MessageIdByName.getId(it.simpleName) ?: it.getConstructor().newInstance().getNetworkMessageId() }
            .mapValues {
                it.value.takeIf { msgs -> msgs.size == 1 }?.get(0)
                    ?: error("Multiple messages for id ${it.key} : ${it.value.joinToString(", ") { msg -> msg.simpleName }} => You're probably missing at least one update of the bot.")
            }
    }

    fun processExport(builders: List<VldbAbstractExportPackTaskBuilder>) {
        val gameDir = VldbFilesUtil.getDofusDirectory()
        val swfFile = File(gameDir, "DofusInvoker.swf")
        if (!swfFile.exists() || !swfFile.isFile) {
            throw RuntimeException("Unable to find DofusInvoker.swf in Dofus directory")
        }
        VldbProtocolUpdater.decompileSwf(swfFile, builders)
    }

    private fun getExportPackBuilders(): List<VldbAbstractExportPackTaskBuilder> {
        return listOf(
            VldbIdByNameExportPackTaskBuilder("MessageReceiver", MessageIdByName, "_messagesTypes"),
            VldbIdByNameExportPackTaskBuilder("ProtocolTypeManager", TypeIdByName, "_typesTypes")
        )
    }

    fun findInetAddress(networkInterfaceName: String): InetAddress? {
        return getNetworkInterfaces().firstOrNull { networkInterfaceName == it.displayName }
            ?.inetAddresses
            ?.asSequence()
            ?.firstOrNull(this::isAddressValid)
    }

    private fun isAddressValid(inetAddress: InetAddress): Boolean {
        return inetAddress.isSiteLocalAddress && !inetAddress.isLoopbackAddress
    }

    private fun getNetworkInterfaces(): List<NetworkInterface> {
        return NetworkInterface.getNetworkInterfaces().asSequence()
            .filter { it.isUp && !it.isLoopback && !it.displayName.contains("VMnet") }
            .toList()
    }

    fun getNetworkInterfaceNames(): List<String> {
        return getNetworkInterfaces().map { it.displayName }
    }
}