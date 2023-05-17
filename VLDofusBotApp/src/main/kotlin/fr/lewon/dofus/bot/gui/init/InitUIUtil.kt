package fr.lewon.dofus.bot.gui.init

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.AppPage
import fr.lewon.dofus.bot.VLDofusBotApp
import fr.lewon.dofus.bot.core.VldbCoreInitializer
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.gui.util.AppInfo
import fr.lewon.dofus.bot.sniffer.DofusMessageReceiverUtil
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.updatePage
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import fr.lewon.dofus.bot.util.filemanagers.impl.GlobalConfigManager
import fr.lewon.dofus.bot.util.listeners.KeyboardListener
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.export.builder.VldbAbstractExportPackTaskBuilder
import org.reflections.Reflections

object InitUIUtil {

    val initTasks = listOf(
        InitTask("Dofus decompiled") {
            DofusMessageReceiverUtil.prepareNetworkManagers(getExportPackBuilders())
        },
        InitTask("${AppInfo.APP_NAME} Core") { initCore() },
        InitTask("File managers") { initFileManagers() },
        InitTask("Sniffer handlers") { initEventStoreHandlers() },
        InitTask("Sniffer network interface") { initSniffer() },
        InitTask("Init Bot UI") { initUIUtils() },
    )
    val initUiState = mutableStateOf(InitUIState(initTasks.first()))

    fun initAll() {
        Thread {
            initUiState.value = initUiState.value.copy(
                executing = true,
                error = ""
            )
            val startIndex = initTasks.indexOf(initUiState.value.currentInitTask)
            for (i in startIndex until initTasks.size) {
                val initTask = initTasks[i]
                initUiState.value = initUiState.value.copy(currentInitTask = initTask)
                startInit(initTask)
                if (!initTask.success) {
                    break
                }
            }
            initUiState.value = initUiState.value.copy(executing = false)
            if (initTasks.all { it.success }) {
                Thread.sleep(1000)
                KeyboardListener.start()
                updatePage(AppPage.MAIN)
            }
        }.start()
    }

    private fun startInit(initTask: InitTask) {
        var error = ""
        try {
            initTask.executionFunction()
            initTask.success = true
        } catch (e: Throwable) {
            e.printStackTrace()
            initTask.success = false
            error = e.message ?: e.toString()
        } finally {
            initUiState.value = initUiState.value.copy(error = error)
        }
    }

    private fun getExportPackBuilders(): List<VldbAbstractExportPackTaskBuilder> {
        return listOf(DecryptionKeyExportPackTaskBuilder)
    }

    private fun initCore() {
        VldbCoreInitializer.initAll(
            DecryptionKeyExportPackTaskBuilder.decryptionKey,
            DecryptionKeyExportPackTaskBuilder.decryptionKeyCharset
        )
    }

    private fun initFileManagers() {
        val managers = Reflections(ToInitManager::class.java.packageName)
            .getSubTypesOf(ToInitManager::class.java)
            .filter { !it.kotlin.isAbstract }
            .mapNotNull { it.kotlin.objectInstance ?: it.getConstructor().newInstance() }

        val initializedManagers = ArrayList<ToInitManager>()
        managers.forEach { initManager(it, initializedManagers) }
    }

    private fun initManager(
        manager: ToInitManager,
        initializedManagers: ArrayList<ToInitManager>
    ) {
        manager.getNeededManagers().forEach {
            initManager(it, initializedManagers)
        }
        if (!initializedManagers.contains(manager)) {
            manager.initManager()
            initializedManagers.add(manager)
        }
    }

    private fun initEventStoreHandlers() {
        Reflections(VLDofusBotApp::class.java.packageName)
            .getSubTypesOf(IEventHandler::class.java)
            .filter { !it.kotlin.isAbstract }
            .mapNotNull { it.kotlin.objectInstance ?: it.getConstructor().newInstance() }
            .forEach { EventStore.addEventHandler(it) }
    }

    private fun initSniffer() {
        val networkInterfaceName = GlobalConfigManager.readConfig().networkInterfaceName
        if (DofusMessageReceiverUtil.findInetAddress(networkInterfaceName) == null) {
            val defaultNetworkInterface = DofusMessageReceiverUtil.getNetworkInterfaceNames().firstOrNull()
                ?: error("No valid network interface found, check your internet connection")
            GlobalConfigManager.editConfig { it.networkInterfaceName = defaultNetworkInterface }
            GameSnifferUtil.updateNetworkInterface()
        }
    }

    private fun initUIUtils() {
        Reflections(ComposeUIUtil::class.java.packageName)
            .getSubTypesOf(ComposeUIUtil::class.java)
            .filter { !it.kotlin.isAbstract }
            .mapNotNull { it.kotlin.objectInstance ?: it.getConstructor().newInstance() }
            .forEach { it.init() }
    }
}