package fr.lewon.dofus.bot.gui2.init

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.AppPage
import fr.lewon.dofus.bot.VLDofusBot
import fr.lewon.dofus.bot.core.VldbCoreInitializer
import fr.lewon.dofus.bot.gui2.util.AppInfo
import fr.lewon.dofus.bot.sniffer.DofusMessageReceiverUtil
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.updatePage
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import fr.lewon.dofus.bot.util.listeners.KeyboardListener
import fr.lewon.dofus.export.builder.VldbAbstractExportPackTaskBuilder
import org.reflections.Reflections

object InitUIState {

    val INIT_SUCCESS = mutableStateOf(false)
    val ERRORS_ON_INIT = mutableStateOf(false)
    val ERRORS = mutableStateOf(ArrayList<String>())


    val INIT_TASKS = listOf(
        InitTask("Dofus decompiled") { DofusMessageReceiverUtil.prepareNetworkManagers(getExportPackBuilders()) },
        InitTask("${AppInfo.APP_NAME} Core") { initCore() },
        InitTask("File managers") { initFileManagers() },
        InitTask("Sniffer handlers") { initEventStoreHandlers() }
    )

    fun initAll() {
        Thread {
            ERRORS_ON_INIT.value = false
            ERRORS.value.clear()
            val toInitTasks = INIT_TASKS.filter { !it.success.value }
            toInitTasks.forEach { it.executed.value = false }
            toInitTasks.forEach { startInit(it) }
            INIT_SUCCESS.value = INIT_TASKS.all { it.success.value }
            ERRORS_ON_INIT.value = !INIT_SUCCESS.value
            if (INIT_SUCCESS.value) {
                Thread.sleep(1000)
                KeyboardListener.start()
                updatePage(AppPage.MAIN)
            }
        }.start()
    }

    private fun startInit(initTask: InitTask) {
        try {
            initTask.executing.value = true
            initTask.function()
            initTask.success.value = true
        } catch (e: Throwable) {
            e.printStackTrace()
            ERRORS.value.add(e.message ?: e.toString())
            initTask.success.value = false
        } finally {
            initTask.executing.value = false
            initTask.executed.value = true
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

}