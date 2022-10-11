package fr.lewon.dofus.bot.gui2.init

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.AppPage
import fr.lewon.dofus.bot.VLDofusBot
import fr.lewon.dofus.bot.core.VldbCoreInitializer
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui2.util.AppInfo
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

    val INIT_UI_STATE = mutableStateOf(InitUIState())

    val INIT_TASKS_UI_STATES = listOf(
        mutableStateOf(InitTaskUIState("Dofus decompiled") {
            DofusMessageReceiverUtil.prepareNetworkManagers(getExportPackBuilders())
        }),
        mutableStateOf(InitTaskUIState("${AppInfo.APP_NAME} Core") { initCore() }),
        mutableStateOf(InitTaskUIState("File managers") { initFileManagers() }),
        mutableStateOf(InitTaskUIState("Sniffer handlers") { initEventStoreHandlers() }),
        mutableStateOf(InitTaskUIState("Sniffer network interface") { initSniffer() })
    )

    fun initAll() {
        Thread {
            INIT_UI_STATE.value = INIT_UI_STATE.value.copy(
                errorsOnInit = false,
                errors = emptyList()
            )
            val toInitTasks = INIT_TASKS_UI_STATES.filter { !it.value.success }
            toInitTasks.forEach { it.value = it.value.copy(executed = false) }
            toInitTasks.forEach { startInit(it) }
            val success = INIT_TASKS_UI_STATES.all { it.value.success }
            INIT_UI_STATE.value = INIT_UI_STATE.value.copy(
                initSuccess = success,
                errorsOnInit = !success
            )
            if (success) {
                CharactersUIUtil.initListeners()
                Thread.sleep(1000)
                KeyboardListener.start()
                updatePage(AppPage.MAIN)
            }
        }.start()
    }

    private fun startInit(initTaskUIState: MutableState<InitTaskUIState>) {
        var success = false
        try {
            initTaskUIState.value = initTaskUIState.value.copy(executing = true)
            initTaskUIState.value.function()
            success = true
        } catch (e: Throwable) {
            e.printStackTrace()
            val initUIState = INIT_UI_STATE.value
            val errorMessage = e.message ?: e.toString()
            INIT_UI_STATE.value = initUIState.copy(errors = initUIState.errors.plus(errorMessage))
        } finally {
            initTaskUIState.value = initTaskUIState.value.copy(executed = true, executing = false, success = success)
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
        Reflections(VLDofusBot::class.java.packageName)
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
        }
        GameSnifferUtil.updateNetworkInterface()
    }
}